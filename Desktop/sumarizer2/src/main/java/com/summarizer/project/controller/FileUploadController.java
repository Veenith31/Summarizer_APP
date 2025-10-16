package com.summarizer.project.controller;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import com.summarizer.project.service.GeminiApiService;
import com.summarizer.project.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/files")
@Tag(name = "File Upload API", description = "Upload PDF and summarize with extracted keywords and related queries")
public class FileUploadController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private SummaryRepository summaryRepository;

    @Operation(summary = "Upload a PDF and get contextual summary, keywords, and related queries ")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPdfAndSummarize(
            @RequestParam("userId") String userId,
            @Parameter(description = "PDF file to upload", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        try {
            String extractedText = pdfService.extractTextFromPdf(file.getInputStream());

            Optional<Summary> existing = summaryRepository.findByUserIdAndOriginalText(userId, extractedText);
            if (existing.isPresent()) {
                return ResponseEntity.ok(existing.get());
            }

            List<String> inputKeywords = geminiApiService.extractKeywords(extractedText);
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
                    + "\n\nNEW INPUT:\n" + extractedText
                    : "Generate a comprehensive and detailed summary of the following text. Expand on key points, provide explanations, and organize the content clearly using bullet points. Avoid asterisks. Use bold headings in Markdown style and start each point on a new line. Do not oversimplify.\n\nText:\n" + extractedText;

            String summaryText = geminiApiService.getSummary(contextualPrompt);
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

            Summary summary = new Summary();
            summary.setUserId(userId);
            summary.setOriginalText(extractedText);
            summary.setSummaryText(summaryText);
            summary.setKeywords(keywords);
            summary.setRelatedQueries(cleanedQueries);
            summary.setMode("pdf");

            summaryRepository.save(summary);

            Map<String, Object> response = new HashMap<>();
            response.put("summary", summaryText);
            response.put("keywords", keywords);
            response.put("relatedQueries", cleanedQueries);
            response.put("prompt", "What would you like to know more about?");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error reading PDF: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error summarizing PDF: " + e.getMessage()));
        }
    }
}

