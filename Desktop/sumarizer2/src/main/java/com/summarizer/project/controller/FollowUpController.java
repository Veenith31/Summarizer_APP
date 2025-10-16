package com.summarizer.project.controller;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import com.summarizer.project.service.GeminiApiService;
import com.summarizer.project.dto.FollowUpRequest;
import com.summarizer.project.dto.FollowUpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/followup")
public class FollowUpController {

    @Autowired
    private SummaryRepository summaryRepository;

    @Autowired
    private GeminiApiService geminiApiService;

    @PostMapping
    public FollowUpResponse followUp(@RequestBody FollowUpRequest request) {
        String userId = request.getUserId(); // email used as userId
        String query = request.getQuery();

        // Step 1: Retrieve all summaries for the user
        List<Summary> userSummaries = summaryRepository.findByUserId(userId);

        // Step 2: Combine relevant summaries
        String context = userSummaries.stream()
                .map(Summary::getSummaryText)
                .collect(Collectors.joining("\n\n"));

        // Step 3: Formulate contextual prompt
        String finalPrompt = "Based on the user's past summaries:\n" +
                context + "\n\nNow answer the following follow-up question:\n" +
                query;

        // Step 4: Call Gemini
        String result = geminiApiService.getSummary(finalPrompt);

        String structuredResult = """
        ### 🔍 Follow-up Response

        - **Context-bsed answer:**  
        %s

        - **Tip:** If the summary feels off, try rephrasing your question.

        ---
    """.formatted(result);


        // Step 5: Return response
        FollowUpResponse response = new FollowUpResponse();
        response.setAnswer(result);
        return response;
    }
}
