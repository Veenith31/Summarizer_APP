//package com.summarizer.project.service;
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.repository.SummaryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Service
//public class SummarizationService {
//
//    @Autowired
//    private GeminiApiService geminiApiService;
//
//    @Autowired
//    private SummaryRepository summaryRepository;
//
//    public Summary summarizeAndStore(String userId, String originalText) {
//        // 1. Generate summary
//        String summaryText = geminiApiService.getSummary(originalText);
//
//        // 2. Extract keywords
//        String keywordResponse = geminiApiService.extractKeywords(originalText);
//        List<String> keywords = Arrays.stream(keywordResponse.split(","))
//                .map(String::trim)
//                .toList();
//
//        // 3. Generate related queries
//        String relatedQueriesResponse = geminiApiService.generateRelatedQueries(summaryText);
//        List<String> relatedQueries = Arrays.stream(relatedQueriesResponse.split("\n"))
//                .map(q -> q.replaceAll("^\\d+\\.|^-", "").trim())
//                .filter(q -> !q.isBlank())
//                .toList();
//
//        // 4. Save in MongoDB
//        Summary summary = new Summary();
//        summary.setUserId(userId);
//        summary.setOriginalText(originalText);
//        summary.setSummaryText(summaryText);
//        summary.setKeywords(keywords.toString());
////        summary.setRelatedQueries(relatedQueries.toString());
//
//        return summaryRepository.save(summary);
//    }
//}