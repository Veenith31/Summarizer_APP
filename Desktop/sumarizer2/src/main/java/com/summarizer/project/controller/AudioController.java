//package com.summarizer.project.controller;
//
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.repository.SummaryRepository;
//import com.summarizer.project.service.GeminiApiService;
//import io.swagger.v3.oas.annotations.media.Schema;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/audio")
//public class AudioController {
//
//    @Autowired
//    private GeminiApiService geminiApiService;
//
//    @Autowired
//    private SummaryRepository summaryRepository;
//
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> uploadAudio(
//            @RequestPart("file") MultipartFile file,
//            @RequestPart("userId") String userId
//    ) {
//        try {
//            String originalText = sendToPythonTranscriber(file);
//            String summaryText = geminiApiService.getSummary(originalText);
//
//            // Save to MongoDB
//            Summary newSummary = new Summary();
//            newSummary.setUserId(userId);
//            newSummary.setOriginalText(originalText);
//            newSummary.setSummaryText(summaryText);
//
//            summaryRepository.save(newSummary);
//
//            return ResponseEntity.ok("Summary saved for user " + userId + ":\n" + summaryText);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error: " + e.getMessage());
//        }
//    }
//
//    private String sendToPythonTranscriber(MultipartFile file) throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
//        body.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
//            @Override
//            public String getFilename() {
//                return file.getOriginalFilename();
//            }
//        });
//
//        HttpEntity<?> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> response = restTemplate.postForEntity(
//                "http://localhost:5000/transcribe", request, String.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return response.getBody();
//        } else {
//            throw new Exception("Transcription failed");
//        }
//    }
//
//    @Schema(name = "AudioUpload", description = "Upload audio and userId")
//    static class AudioUploadSchema {
//        @Schema(type = "string", format = "binary")
//        public MultipartFile file;
//
//        @Schema(type = "string", description = "User ID")
//        public String userId;
//    }
//}

package com.summarizer.project.controller;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import com.summarizer.project.service.GeminiApiService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/audio")
//@CrossOrigin(origins = "*")
public class AudioController {

    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private SummaryRepository summaryRepository;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAudio(
            @RequestPart("file") MultipartFile file,
            @RequestPart("userId") String userId
    ) {
        try {
            // Step 1: Transcribe audio using Python service
            String originalText = sendToPythonTranscriber(file);

            // Step 2: Check for existing summary
            Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, originalText);
            if (existing.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "summary", existing.get().getSummaryText(),
                        "keywords", existing.get().getKeywords(),
                        "relatedQueries", existing.get().getRelatedQueries(),
                        "prompt", "What would you like to know more about?"
                ));
            }

            // Step 3: Extract keywords from original text
            List<String> inputKeywords = geminiApiService.extractKeywords(originalText);

            // Step 4: Fetch past summaries for context
            List<Summary> pastSummaries = summaryRepository.findByUserId(userId);
            StringBuilder contextBuilder = new StringBuilder();

            for (Summary past : pastSummaries) {
                List<String> pastKeywords = past.getKeywords() != null ? past.getKeywords() : new ArrayList<>();
                for (String keyword : inputKeywords) {
                    for (String pastKeyword : pastKeywords) {
                        if (pastKeyword != null && pastKeyword.equalsIgnoreCase(keyword)) {
                            contextBuilder.append(past.getSummaryText()).append("\n");
                            break;
                        }
                    }
                }
            }

            // Step 5: Construct contextual prompt
            String contextualPrompt = contextBuilder.length() > 0
                    ? "You are an expert summarizer. Use the CONTEXT below to enrich the understanding of the NEW INPUT. Extract key insights from both and write a contextual summary and write some points and suggest some related resources.\n\n"
                    + "CONTEXT:\n" + contextBuilder.toString().trim() + "\n\nNEW INPUT:\n" + originalText
                    : originalText;

            // Step 6: Get summary from Gemini
            String summaryText = geminiApiService.getSummary(contextualPrompt);

            // Step 7: Extract keywords & related queries
            List<String> keywords = geminiApiService.extractKeywords(summaryText);
            List<String> relatedQueries = geminiApiService.generateRelatedQueries(summaryText);

            // Step 8: Save to MongoDB
            Summary newSummary = new Summary();
            newSummary.setUserId(userId);
            newSummary.setOriginalText(originalText);
            newSummary.setSummaryText(summaryText);
            newSummary.setKeywords(keywords);
            newSummary.setRelatedQueries(relatedQueries);
            newSummary.setMode("audio");

            summaryRepository.save(newSummary);

            // Step 9: Return structured response
            Map<String, Object> response = new HashMap<>();
            response.put("summary", summaryText);
            response.put("keywords", keywords);
            response.put("relatedQueries", relatedQueries);
            response.put("prompt", "What would you like to know more about?");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error during audio upload: " + e.getMessage()));
        }
    }

    private String sendToPythonTranscriber(MultipartFile file) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        body.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:5000/transcribe", request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new Exception("Transcription failed with status: " + response.getStatusCode());
        }
    }

    @Schema(name = "AudioUpload", description = "Upload audio and userId")
    static class AudioUploadSchema {
        @Schema(type = "string", format = "binary")
        public MultipartFile file;

        @Schema(type = "string", description = "User ID")
        public String userId;
    }
}
