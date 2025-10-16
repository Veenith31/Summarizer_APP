//package com.summarizer.project.controller;
//
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.repository.SummaryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/search")
//@CrossOrigin(origins = "*")
//public class SearchController {
//
//    @Autowired
//    private SummaryRepository summaryRepository;
//
//    @GetMapping
//    public List<Summary> searchSummaries(@RequestParam String userId, @RequestParam String keyword) {
//        List<Summary> summaries = summaryRepository.findByUserId(userId);
//        return summaries.stream()
//                .filter(summary -> summary.getKeywords() != null && summary.getKeywords().contains(keyword.toLowerCase()))
//                .toList();
//    }
//}

package com.summarizer.project.controller;

import com.summarizer.project.model.Summary;
import com.summarizer.project.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SearchController {

    @Autowired
    private SummaryRepository summaryRepository;

    // For keyword search
    @GetMapping
    public List<Summary> searchSummaries(@RequestParam String userId, @RequestParam String keyword) {
        List<Summary> summaries = summaryRepository.findByUserId(userId);
        return summaries.stream()
                .filter(summary -> summary.getKeywords() != null && summary.getKeywords().contains(keyword.toLowerCase()))
                .toList();
    }

    // ✅ For getting all summaries for a user
    @GetMapping("/user/{userId}")
    public List<Summary> getSummariesByUserId(@PathVariable String userId) {
        return summaryRepository.findByUserId(userId);
    }
}
