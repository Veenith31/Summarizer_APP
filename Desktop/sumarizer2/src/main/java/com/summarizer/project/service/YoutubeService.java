//package com.summarizer.project.service;
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.repository.SummaryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URI;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class YoutubeService {
//
//    @Autowired
//    private GeminiApiService geminiApiService;
//
//    @Autowired
//    private SummaryRepository summaryRepository;
//
//    public Summary processYoutubeVideo(String url, String userId) {
//        String videoId = extractVideoId(url);
//        String captions = fetchCaptionsFromPythonAPI(videoId);
//        String summaryText = geminiApiService.getSummary(captions);
//
//        Summary summary = new Summary();
//        summary.setUserId(userId);
//        summary.setOriginalText(captions);
//        summary.setSummaryText(summaryText);
// summary.setKeywords(geminiApiService.extractKeywords(summaryText));
//        summary.setRelatedQueries(geminiApiService.generateRelatedQueries(String.valueOf(summary)));
//
//        return summaryRepository.save(summary);
//    }
//
//    private String extractVideoId(String url) {
//        try {
//            URI uri = new URI(url);
//            String query = uri.getQuery();
//            if (query != null && query.contains("v=")) {
//                return query.split("v=")[1].split("&")[0];
//            } else {
//                return uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
//            }
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid YouTube URL");
//        }
//    }
//
//    private String fetchCaptionsFromPythonAPI(String videoId) {
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            String pythonApiUrl = "http://localhost:5001/captions?videoId=" + videoId;
//            return restTemplate.getForObject(pythonApiUrl, String.class);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch captions from Python service", e);
//        }
//    }

/*
package com.summarizer.project.service;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Service
public class YoutubeService {

    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private SummaryRepository summaryRepository;

    @Value("${python.api.url:http://localhost:5000}")
    private String pythonApiBaseUrl;

    public Map<String, Object> processYoutubeVideo(String url, String userId) {
        try {
            // Step 1: Extract Video ID and get captions
            String videoId = extractVideoId(url);
            String captions = fetchCaptionsFromPythonAPI(videoId);

            // Step 2: Check if summary already exists
            Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, captions);
            if (existing.isPresent()) {
                Summary cached = existing.get();
                return Map.of(
                        "summary", cached.getSummaryText(),
                        "keywords", cached.getKeywords(),
                        "relatedQueries", cached.getRelatedQueries(),
                        "prompt", "What would you like to know more about?"
                );
            }

            // Step 3: Extract keywords from captions
            List<String> inputKeywords = geminiApiService.extractKeywords(captions);

            // Step 4: Build contextual prompt from previous summaries
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
                    ? "You are an expert summarizer. Use the CONTEXT below to enrich the understanding of the NEW INPUT. Extract key insights from both and write a concise contextual summary.\n\n"
                    + "CONTEXT:\n" + contextBuilder.toString().trim() + "\n\nNEW INPUT:\n" + captions
                    : captions;

            // Step 5: Get contextual summary
            String summaryText = geminiApiService.getSummary(contextualPrompt);

            // Step 6: Extract keywords and related queries
            List<String> keywords = geminiApiService.extractKeywords(summaryText);
            List<String> relatedQueries = geminiApiService.generateRelatedQueries(summaryText);

            // Step 7: Save to MongoDB
            Summary summary = new Summary();
            summary.setUserId(userId);
            summary.setOriginalText(captions);
            summary.setSummaryText(summaryText);
            summary.setKeywords(keywords);
            summary.setRelatedQueries(relatedQueries);
            summaryRepository.save(summary);

            // Step 8: Build and return response
            return Map.of(
                    "summary", summaryText,
                    "keywords", keywords,
                    "relatedQueries", relatedQueries,
                    "prompt", "What would you like to know more about?"
            );

        } catch (Exception e) {
            throw new RuntimeException("Error processing YouTube video", e);
        }
    }

    private String extractVideoId(String url) {
        try {
            URI uri = new URI(url);

            // Check standard URL format (youtube.com/watch?v=ID)
            String query = uri.getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("v=")) {
                        return param.substring(2);
                    }
                }
            }

            // Check short URL format (youtu.be/ID)
            String path = uri.getPath();
            if (path != null && path.length() > 1) {
                return path.substring(1);
            }

            throw new IllegalArgumentException("Could not extract video ID from URL");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid YouTube URL", e);
        }
    }

    private String fetchCaptionsFromPythonAPI(String videoId) {
        RestTemplate restTemplate = new RestTemplate();
        String pythonApiUrl = pythonApiBaseUrl + "/captions?videoId=" + videoId;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(pythonApiUrl, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if (responseBody.containsKey("error")) {
                    throw new RuntimeException("Python API error: " + responseBody.get("message"));
                }

                if (responseBody.containsKey("captions")) {
                    return (String) responseBody.get("captions");
                }
            }

            throw new RuntimeException("Failed to fetch captions: " + response.getStatusCode());

        } catch (Exception e) {
            throw new RuntimeException("Python API request failed: " + e.getMessage(), e);
        }
    }
}
*/

//    private String fetchCaptionsFromPythonAPI(String videoId) {
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            String pythonApiUrl = "http://localhost:5001/captions?videoId=" + videoId;
//
//            Map<?, ?> response = restTemplate.getForObject(pythonApiUrl, Map.class);
//            if (response.containsKey("translated")) {
//                return (String) response.get("translated");
//            } else {
//                throw new RuntimeException("Translation failed: " + response.get("error"));
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch translated captions from Python service", e);
//        }
//    }


package com.summarizer.project.service;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Service
public class YoutubeService {

    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private SummaryRepository summaryRepository;

    @Value("${python.api.url:http://localhost:5000}")
    private String pythonApiBaseUrl;

    public Map<String, Object> processYoutubeVideo(String url, String userId) {
        try {
            // Step 1: Extract Video ID and get captions
            String videoId = extractVideoId(url);
            String captions = fetchCaptionsFromPythonAPI(videoId);

            // Step 2: Check if summary already exists
            Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, captions);
            if (existing.isPresent()) {
                Summary cached = existing.get();
                return Map.of(
                        "summary", cached.getSummaryText(),
                        "keywords", cached.getKeywords(),
                        "relatedQueries", cached.getRelatedQueries(),
                        "prompt", "What would you like to know more about?"
                );
            }

            // Step 3: Extract keywords from captions
            List<String> inputKeywords = geminiApiService.extractKeywords(captions);

            // Step 4: Build contextual prompt from previous summaries
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
                    ? "You are an expert summarizer. Use the CONTEXT below to enrich the understanding of the NEW INPUT. Extract key insights from both and write a concise contextual summary and write some points and suggest some related resources.\n\n"
                    + "CONTEXT:\n" + contextBuilder.toString().trim() + "\n\nNEW INPUT:\n" + captions
                    : captions;

            // Step 5: Get contextual summary
            String summaryText = geminiApiService.getSummary(contextualPrompt);

            // Step 6: Extract keywords and related queries
            List<String> keywords = geminiApiService.extractKeywords(summaryText);
            List<String> rawQueries = geminiApiService.generateRelatedQueries(summaryText);
            List<String> cleanedQueries = new ArrayList<>();

            for (String query : rawQueries) {
                String cleaned = query
                        .replaceAll("\\*+", "")           // Remove asterisks
                        .replaceAll("\\s{2,}", " ")       // Collapse multiple spaces
                        .trim();                          // Trim edges

                // Optional: truncate to 15 words max
                String[] words = cleaned.split("\\s+");
                if (words.length > 15) {
                    cleaned = String.join(" ", Arrays.copyOfRange(words, 0, 15)) + "...";
                }

                cleanedQueries.add(cleaned);
            }
            // Step 7: Save to MongoDB
            Summary summary = new Summary();
            summary.setUserId(userId);
            summary.setOriginalText(captions);
            summary.setSummaryText(summaryText);
            summary.setKeywords(keywords);
            summary.setRelatedQueries(cleanedQueries);
            summary.setMode("youtube"); // ✅ NEW MODE TAG

            summaryRepository.save(summary);

            // Step 8: Build and return response
            return Map.of(
                    "summary", summaryText,
                    "keywords", keywords,
                    "relatedQueries", cleanedQueries,
                    "prompt", "What would you like to know more about?"
            );

        } catch (Exception e) {
            throw new RuntimeException("Error processing YouTube video", e);
        }
    }

    private String extractVideoId(String url) {
        try {
            URI uri = new URI(url);

            // Standard URL: youtube.com/watch?v=ID
            String query = uri.getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("v=")) {
                        return param.substring(2);
                    }
                }
            }

            // Short URL: youtu.be/ID
            String path = uri.getPath();
            if (path != null && path.length() > 1) {
                return path.substring(1);
            }

            throw new IllegalArgumentException("Could not extract video ID from URL");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid YouTube URL", e);
        }
    }

    private String fetchCaptionsFromPythonAPI(String videoId) {
        RestTemplate restTemplate = new RestTemplate();
        String pythonApiUrl = pythonApiBaseUrl + "/captions?videoId=" + videoId;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(pythonApiUrl, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if (responseBody.containsKey("error")) {
                    throw new RuntimeException("Python API error: " + responseBody.get("message"));
                }

                if (responseBody.containsKey("captions")) {
                    return (String) responseBody.get("captions");
                }
            }

            throw new RuntimeException("Failed to fetch captions: " + response.getStatusCode());

        } catch (Exception e) {
            throw new RuntimeException("Python API request failed: " + e.getMessage(), e);
        }
    }
}
