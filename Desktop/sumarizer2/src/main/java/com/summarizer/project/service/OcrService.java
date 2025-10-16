//package com.summarizer.project.service;
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.repository.SummaryRepository;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//
//@Service
//public class OcrService {
//
//    @Autowired
//    private GeminiApiService geminiApiService;
//
//    @Autowired
//    private SummaryRepository summaryRepository;
//
//    public Summary processImage(MultipartFile file, String userId) {
//        try {
//            // Convert file to BufferedImage
//            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
//            if (bufferedImage == null) {
//                throw new RuntimeException("Failed to decode image: format not supported or corrupted.");
//            }
//
//            // OCR
//            Tesseract tesseract = new Tesseract();
//            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
//            String extractedText = tesseract.doOCR(bufferedImage);
//
//            // Send to Gemini API for summary
//            String summaryText = geminiApiService.getSummary(extractedText);
//
//            // Save to DB
//            Summary summary = new Summary();
//            summary.setUserId(userId);
//            summary.setOriginalText(extractedText);
//            summary.setSummaryText(summaryText);
//            summaryRepository.save(summary);
//
//            return summary;
//
//        } catch (IOException | TesseractException e) {
//            throw new RuntimeException("Failed to process image with OCR", e);
//        }
//    }
//}

/*
package com.summarizer.project.service;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

@Service
public class OcrService {

    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private SummaryRepository summaryRepository;

    public Map<String, Object> processImage(MultipartFile file, String userId) {
        try {
            // Step 1: OCR - extract text from image
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new RuntimeException("Invalid or unsupported image format.");
            }

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata"); // Adjust path if needed
            String extractedText = tesseract.doOCR(image);

            // Step 2: Check for existing summary
            Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, extractedText);
            if (existing.isPresent()) {
                return Map.of(
                        "summary", existing.get().getSummaryText(),
                        "keywords", existing.get().getKeywords(),
                        "relatedQueries", existing.get().getRelatedQueries(),
                        "prompt", "What would you like to know more about?"
                );
            }

            // Step 3: Extract keywords from OCR text
            List<String> inputKeywords = geminiApiService.extractKeywords(extractedText);

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

            // Step 5: Build contextual prompt
            String contextualPrompt = contextBuilder.length() > 0
                    ? "You are an expert summarizer. Use the CONTEXT below to enrich the understanding of the NEW INPUT. Extract key insights from both and write a concise contextual summary.\n\n"
                    + "CONTEXT:\n" + contextBuilder.toString().trim() + "\n\nNEW INPUT:\n" + extractedText
                    : extractedText;

            // Step 6: Generate summary using Gemini
            String summaryText = geminiApiService.getSummary(contextualPrompt);

            // Step 7: Extract keywords & related queries
            List<String> keywords = geminiApiService.extractKeywords(summaryText);
            List<String> relatedQueries = geminiApiService.generateRelatedQueries(summaryText);

            // Step 8: Save to MongoDB
            Summary summary = new Summary();
            summary.setUserId(userId);
            summary.setOriginalText(extractedText);
            summary.setSummaryText(summaryText);
            summary.setKeywords(keywords);
            summary.setRelatedQueries(relatedQueries);
            summaryRepository.save(summary);

            // Step 9: Return structured response
            Map<String, Object> response = new HashMap<>();
            response.put("summary", summaryText);
            response.put("keywords", keywords);
            response.put("relatedQueries", relatedQueries);
            response.put("prompt", "What would you like to know more about?");
            return response;

        } catch (IOException | TesseractException e) {
            throw new RuntimeException("Failed to process image with OCR", e);
        }
    }
}


 */

        package com.summarizer.project.service;

        import com.summarizer.project.model.Summary;
        import com.summarizer.project.repository.SummaryRepository;
        import net.sourceforge.tess4j.Tesseract;
        import net.sourceforge.tess4j.TesseractException;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.web.multipart.MultipartFile;

        import javax.imageio.ImageIO;
        import java.awt.image.BufferedImage;
        import java.io.IOException;
        import java.util.*;

        @Service
        public class OcrService {

            @Autowired
            private GeminiApiService geminiApiService;

            @Autowired
            private SummaryRepository summaryRepository;

            public Map<String, Object> processImage(MultipartFile file, String userId) {
                try {
                    // Step 1: OCR - extract text from image
                    BufferedImage image = ImageIO.read(file.getInputStream());
                    if (image == null) {
                        throw new RuntimeException("Invalid or unsupported image format.");
                    }

                    Tesseract tesseract = new Tesseract();
                    tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
                    String extractedText = tesseract.doOCR(image);

                    // Step 2: Check if summary exists
                    Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, extractedText);
                    if (existing.isPresent()) {
                        return Map.of(
                                "summary", existing.get().getSummaryText(),
                                "keywords", existing.get().getKeywords(),
                                "relatedQueries", existing.get().getRelatedQueries(),
                                "prompt", "What would you like to know more about?"
                        );
                    }

                    // Step 3: Extract keywords from extracted text
                    List<String> inputKeywords = geminiApiService.extractKeywords(extractedText);

                    // Step 4: Retrieve previous summaries by user and compare
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

                    // Step 5: Build contextual prompt
        //            String contextualPrompt = contextBuilder.length() > 0
        //                    ? "You are an expert summarizer. Use the CONTEXT below to enrich the understanding of the NEW INPUT. Extract key insights from both and write a concise contextual summary and write some points and suggest some related resources.\n\n"
        //                    + "CONTEXT:\n" + contextBuilder.toString().trim() + "\n\nNEW INPUT:\n" + extractedText
        //                    : extractedText;

                    String contextualPrompt = contextBuilder.length() > 0
                            ? "You are an expert summarizer and educator. Use the CONTEXT below to deeply analyze and enrich the NEW INPUT. Combine insights from both CONTEXT and NEW INPUT to produce a comprehensive, structured summary. Expand on key points, include explanations, and organize using bullet points. Use bold Markdown formatting for section headings (e.g., **Main Points**, **Insights**, **Resources**). Avoid asterisks (*). Each point should be on a new line and detailed. At the end, suggest relevant learning resources or references.\n\n"
                            + "CONTEXT:\n" + contextBuilder.toString().trim()
                            + "\n\nNEW INPUT:\n" + extractedText
                            : "Analyze the following text and create a detailed, structured summary. Expand on all key points with explanations. Use bullet points with bold headings in Markdown style. Avoid asterisks. End with useful related learning resources.\n\nText:\n" + extractedText;


                    // Step 6: Get summary from Gemini
                    String summaryText = geminiApiService.getSummary(contextualPrompt);

                    // Step 7: Extract keywords & related queries
                    List<String> keywords = geminiApiService.extractKeywords(summaryText);
                    List<String> relatedQueries = geminiApiService.generateRelatedQueries(summaryText);

                    // Step 8: Save to MongoDB
                    Summary summary = new Summary();
                    summary.setUserId(userId);
                    summary.setOriginalText(extractedText);
                    summary.setSummaryText(summaryText);
                    summary.setKeywords(keywords);
                    summary.setRelatedQueries(relatedQueries);
                    summary.setMode("image");
                    summaryRepository.save(summary);

                    // Step 9: Return response
                    Map<String, Object> response = new HashMap<>();
                    response.put("summary", summaryText);
                    response.put("keywords", keywords);
                    response.put("relatedQueries", relatedQueries);
                    response.put("prompt", "What would you like to know more about?");

                    return response;

                } catch (IOException | TesseractException e) {
                    throw new RuntimeException("Failed to process image with OCR", e);
                }
            }
        }
