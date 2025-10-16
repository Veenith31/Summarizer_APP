package com.summarizer.project.controller;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/summaries")
public class UserSummaryController {

    @Autowired
    private SummaryRepository summaryRepository;

    // ✅ GET all summaries by userId and mode
    @GetMapping("/by-mode")
    public ResponseEntity<?> getSummariesByUserIdAndMode(
            @RequestParam("userId") String userId,
            @RequestParam("mode") String mode
    ) {
        try {
            List<Summary> summaries = summaryRepository.findByUserIdAndMode(userId, mode);
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch summaries", "message", e.getMessage()));
        }
    }
}
