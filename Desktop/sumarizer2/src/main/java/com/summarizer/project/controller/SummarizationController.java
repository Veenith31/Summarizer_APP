//package com.summarizer.project.controller;
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.repository.SummaryRepository;
//import com.summarizer.project.service.GeminiApiService;
////import com.summarizer.project.service.SummarizationService;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.web.bind.annotation.*;
//
//
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/summarize")
//@CrossOrigin(origins = "*")
//public class SummarizationController {
//
//    @Autowired
//    private GeminiApiService geminiApiService;
//
//    @Autowired
//    private SummaryRepository summaryRepository;
//
//    @PostMapping
//    public String summarizeText(@RequestParam String userId, @RequestParam String text) {
//
//        Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, text);
//
//        if (existing.isPresent()) {
//            return existing.get().getSummaryText();
//        }
//
////    public ResponseEntity<?> summarizeAndAnalyze(@RequestBody Map<String, String> request) {
////        String originalText = request.get("text");
////        String userId = request.get("userId"); // Optional
////
//        try {
//            String summary = geminiApiService.getSummary(text);
//
////        String keywords = geminiApiService.extractKeywords(text);
////
////        String relatedInfo = geminiApiService.generateRelatedQueries(summary);
//
////            // 2. Call Flask NLP microservice
////            RestTemplate restTemplate = new RestTemplate();
////            HttpHeaders headers = new HttpHeaders();
////            headers.setContentType(MediaType.APPLICATION_JSON);
////            Map<String, String> payload = new HashMap<>();
////            payload.put("summary", summary);
////
////            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);
////            String flaskUrl = "http://localhost:5001/analyze"; // Flask NLP endpoint
////            ResponseEntity<Map> nlpResponse = restTemplate.postForEntity(flaskUrl, entity, Map.class);
////
////            List<String> keywords = (List<String>) nlpResponse.getBody().get("keywords");
////            List<String> relatedQueries = (List<String>) nlpResponse.getBody().get("related_queries");
//
//
//            //String keywords = extractKeywords(text);
//
//            //  Save to MongoDB
//            Summary newSummary = new Summary();
//            newSummary.setUserId(userId);
//            newSummary.setOriginalText(text);
//            newSummary.setSummaryText(summary);
//            //     newSummary.setRelatedQueries(relatedInfo);
//            //  newSummary.setKeywords(keywords.toString());
//
//            summaryRepository.save(newSummary);
//            return summary;
//
//
//        } catch (Exception e) {
//            return ("Error occurrd while summarizing text: ");
//        }
////            // 4. Return response
////            Map<String, Object> response = new HashMap<>();
////            response.put("summary", summary);
////            response.put("keywords", keywords);
////            response.put("relatedQueries", relatedQueries);
////            response.put("prompt", "What would you like to know more about?");
////
////            return ResponseEntity.ok(response);
////
////
////        }
////        catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .body(Collections.singletonMap("error", e.getMessage()));
////       }
////    }
//
//
//        // ✂ Simple keyword extraction
//    /*private String extractKeywords(String text) {
//        String[] words = text.split("\\s+");
//        StringBuilder keywords = new StringBuilder();
//        for (int i = 0; i < Math.min(5, words.length); i++) {
//            keywords.append(words[i]).append(",");
//        }
//        return keywords.toString();
//    }
//
//     */
//    }
//}
//


//package com.summarizer.project.controller;
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.repository.SummaryRepository;
//import com.summarizer.project.service.GeminiApiService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/summarize")
//@CrossOrigin(origins = "*")
//public class SummarizationController {
//
//    @Autowired
//    private GeminiApiService geminiApiService;
//
//    @Autowired
//    private SummaryRepository summaryRepository;
//
//    @PostMapping
//    public ResponseEntity<?> summarizeText(@RequestParam String userId, @RequestParam String text) {
//
//        Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, text);
//        if (existing.isPresent()) {
//            return ResponseEntity.ok(existing.get()); // return cached summary
//        }
//
//        try {
//            // 1. Summarize using Gemini API
//            String summary = geminiApiService.getSummary(text);
//
//            // 2. Send summary to Flask NLP microservice
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            Map<String, String> payload = new HashMap<>();
//            payload.put("summary", summary);
//
//            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
//            String flaskUrl = "http://localhost:5001/analyze"; // Flask NLP endpoint
//            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, request, Map.class);
//
//            // Extract NLP results
//            List<String> keywords = (List<String>) response.getBody().get("keywords");
//            List<String> relatedQueries = (List<String>) response.getBody().get("related_queries");
//
//            // 3. Save to MongoDB
//            Summary newSummary = new Summary();
//            newSummary.setUserId(userId);
//            newSummary.setOriginalText(text);
//            newSummary.setSummaryText(summary);
//            newSummary.setKeywords(keywords.toString());
//           // newSummary.setRelatedQueries(relatedQueries);
//
//            summaryRepository.save(newSummary);
//
//            // 4. Return structured response
//            Map<String, Object> finalResponse = new HashMap<>();
//            finalResponse.put("summary", summary);
//            finalResponse.put("keywords", keywords);
//            finalResponse.put("relatedQueries", relatedQueries);
//            finalResponse.put("prompt", "What would you like to know more about?");
//
//            return ResponseEntity.ok(finalResponse);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", "Error occurred while summarizing text: " + e.getMessage()));
//        }
//    }
//}

//package com.summarizer.project.controller;
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.repository.SummaryRepository;
//import com.summarizer.project.service.GeminiApiService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/summarize")
//@CrossOrigin(origins = "*")
//public class SummarizationController {
//
//    @Autowired
//    private GeminiApiService geminiApiService;
//
//    @Autowired
//    private SummaryRepository summaryRepository;
//
//    @PostMapping
//    public ResponseEntity<?> summarizeText(@RequestParam String userId, @RequestParam String text) {
//        Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, text);
//        if (existing.isPresent()) {
//            return ResponseEntity.ok(existing.get()); // return cached summary
//        }
//
//        try {
//
//
////            // 1. Summarize using Gemini API
////            String summary = geminiApiService.getSummary(text);
////
////            // 2. Extract keywords and related queries from summary
////            List<String> keywords = geminiApiService.extractKeywords(summary);
////            List<String> relatedQueries = geminiApiService.generateRelatedQueries(summary);
//            // Step 1: Get keywords from the new input
//            List<String> initialKeywords = geminiApiService.extractKeywords(text);
//
//            // Step 2: Retrieve past summaries and build context based on matching keywords
//            List<Summary> pastSummaries = summaryRepository.findByUserId(userId);
//            StringBuilder contextBuilder = new StringBuilder();
//
//            for (Summary s : pastSummaries) {
//                for (String keyword : initialKeywords) {
//                    if (s.getKeywords() != null && s.getKeywords().toString().contains(keyword)) {
//                        contextBuilder.append(s.getSummaryText()).append("\n");
//                        break;
//                    }
//                }
//            }
//
//            // Step 3: Build contextual prompt
//            String contextualPrompt = contextBuilder.length() > 0
//                    ? "Use the following context and new text to generate a contextual summary.\n\nContext:\n"
//                    + contextBuilder.toString() + "\nNew Text:\n" + text
//                    : text;
//
//            // Step 4: Generate summary using Gemini API
//            String summary = geminiApiService.getSummary(contextualPrompt);
//
//            // Step 5: Extract keywords and related queries from the new summary
//            List<String> keywords = geminiApiService.extractKeywords(summary);
//            List<String> relatedQueries = geminiApiService.generateRelatedQueries(summary);
//
//            // 3. Save to MongoDB
//            Summary newSummary = new Summary();
//            newSummary.setUserId(userId);
//            newSummary.setOriginalText(text);
//            newSummary.setSummaryText(summary);
//            newSummary.setKeywords(keywords);
//            newSummary.setRelatedQueries(relatedQueries);
//
//            summaryRepository.save(newSummary);
//
//            // 4. Return structured response
//            Map<String, Object> finalResponse = new HashMap<>();
//            finalResponse.put("summary", summary);
//            finalResponse.put("keywords", keywords);
//            finalResponse.put("relatedQueries", relatedQueries);
//            finalResponse.put("prompt", "What would you like to know more about?");
//
//            return ResponseEntity.ok(finalResponse);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", "Error occurred while summarizing text: " + e.getMessage()));
//        }
//    }

/*
package com.summarizer.project.controller;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import com.summarizer.project.dto.SummarizeRequest;
import com.summarizer.project.service.GeminiApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/summarize")
//@CrossOrigin(origins = "*")
public class SummarizationController {

    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private SummaryRepository summaryRepository;

    @PostMapping

    public ResponseEntity<?> summarizeText(@RequestBody SummarizeRequest request) {
        String userId = request.getUserId();
        String text = request.getText();
        {
            try {
                // Return existing summary if present
                Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, text);
                if (existing.isPresent()) {
                    return ResponseEntity.ok(existing.get());
                }

                // Step 1: Extract keywords from the input text
                List<String> inputKeywords = geminiApiService.extractKeywords(text);

                // Step 2: Retrieve user's previous summaries
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

                // Step 3: Prepare contextual prompt
//            String contextualPrompt = contextBuilder.length() > 0
//                    ? "Use the following context and the new text to create a contextual summary.\n\nContext:\n"
//                    + contextBuilder.toString().trim() + "\n\nNew Text:\n" + text
//                    : text;

                String contextualPrompt = contextBuilder.length() > 0
                        ? "You are an expert summarizer. Use the CONTEXT below to enrich the understanding of the NEW INPUT. Extract key insights from both and write a concise contextual summary.\n\n"
                        + "CONTEXT:\n" + contextBuilder.toString().trim() + "\n\nNEW INPUT:\n" + text
                        : text;


                // Step 4: Generate contextual summary
                System.out.println("=== CONTEXTUAL PROMPT ===\n" + contextualPrompt);

                String summary = geminiApiService.getSummary(contextualPrompt);

                // Step 5: Extract keywords and related queries from new summary
                List<String> keywords = geminiApiService.extractKeywords(summary);
                List<String> relatedQueries = geminiApiService.generateRelatedQueries(summary);

                // Step 6: Save to MongoDB
                Summary newSummary = new Summary();
                newSummary.setUserId(userId);
                newSummary.setOriginalText(text);
                newSummary.setSummaryText(summary);
                newSummary.setKeywords(keywords);
                newSummary.setRelatedQueries(relatedQueries);

                summaryRepository.save(newSummary);

                // Step 7: Return structured response
                Map<String, Object> finalResponse = new HashMap<>();
                finalResponse.put("summary", summary);
                finalResponse.put("keywords", keywords);
                finalResponse.put("relatedQueries", relatedQueries);
                finalResponse.put("prompt", "What would you like to know more about?");

                return ResponseEntity.ok(finalResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Error occurred while summarizing text: " + e.getMessage()));
            }
        }
    }
}

*/
package com.summarizer.project.controller;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import com.summarizer.project.dto.SummarizeRequest;
import com.summarizer.project.service.GeminiApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/summarize")
public class SummarizationController {

    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private SummaryRepository summaryRepository;


    @PostMapping
    public ResponseEntity<?> summarizeText(@RequestBody SummarizeRequest request) {
        String userId = request.getUserId();
        String text = request.getText();
        String mode = request.getMode();

        try {
            // Return existing summary if present
            Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, text);
            if (existing.isPresent()) {
                return ResponseEntity.ok(existing.get());
            }

            // Step 1: Extract keywords from the input text
            List<String> inputKeywords = geminiApiService.extractKeywords(text);

            // Step 2: Retrieve user's previous summaries
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


            String contextualPrompt = contextBuilder.length() > 0
                    ? "You are an expert summarizer and educator. Use the CONTEXT below to deeply enrich and elaborate on the NEW INPUT. Generate a comprehensive and detailed summary, expanding on key concepts, examples, and any implicit information. Use bullet points for clarity. Do not use asterisks (*). Use bold Markdown formatting for headings (e.g., **Topic**). Ensure each point starts on a new line and provide in-depth explanation where necessary. Avoid overly brief points.\n\n"
                    + "CONTEXT:\n" + contextBuilder.toString().trim()
                    + "\n\nNEW INPUT:\n" + text
                    : "Generate a comprehensive and detailed summary of the following text. Expand on key points, provide explanations, and organize the content clearly using bullet points. Avoid asterisks. Use bold headings in Markdown style and start each point on a new line. Do not oversimplify.\n\nText:\n" + text;


            // Step 4: Generate contextual summary
            System.out.println("=== CONTEXTUAL PROMPT ===\n" + contextualPrompt);
            String summary = geminiApiService.getSummary(contextualPrompt);

            // Step 5: Extract keywords and related queries
            List<String> keywords = geminiApiService.extractKeywords(summary);
            List<String> rawQueries = geminiApiService.generateRelatedQueries(summary);
            List<String> cleanedQueries = new ArrayList<>();

            for (String query : rawQueries) {
                String cleaned = query
                        .replaceAll("\\*+", "")           // Remove asterisks
                        .replaceAll("\\s{2,}", " ")       // Collapse multiple spaces
                        .trim();                          // Trim edges

                //  truncatING to 15 words max
                String[] words = cleaned.split("\\s+");
                if (words.length > 15) {
                    cleaned = String.join(" ", Arrays.copyOfRange(words, 0, 15)) + "...";
                }

                cleanedQueries.add(cleaned);
            }

            // Step 6: Save to MongoDB
            Summary newSummary = new Summary();
            newSummary.setUserId(userId);
            newSummary.setOriginalText(text);
            newSummary.setSummaryText(summary);
            newSummary.setKeywords(keywords);
            newSummary.setRelatedQueries(cleanedQueries);
            newSummary.setMode(mode);

            summaryRepository.save(newSummary);

            // Step 7: Return response
            Map<String, Object> finalResponse = new HashMap<>();
            finalResponse.put("summary", summary);
            finalResponse.put("keywords", keywords);
            finalResponse.put("relatedQueries", cleanedQueries);
            finalResponse.put("prompt", "What would you like to know more about?");

            return ResponseEntity.ok(finalResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error occurred while summarizing text: " + e.getMessage()));
        }
    }

    // Get All Summaries for User by Mode
    @GetMapping("/history")
    public ResponseEntity<?> getUserHistoryByMode(@RequestParam String userId, @RequestParam String mode) {
        try {
            List<Summary> summaries = summaryRepository.findByUserIdAndMode(userId, mode);
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Could not fetch history: " + e.getMessage()));
        }
    }
}
