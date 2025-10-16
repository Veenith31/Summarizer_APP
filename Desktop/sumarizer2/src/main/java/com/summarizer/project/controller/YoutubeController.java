//package com.summarizer.project.controller;
//
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.service.YoutubeService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@Tag(name = "YouTube Summarizer", description = "Extract captions and summarize YouTube video")
//@RestController
//@RequestMapping("/api/youtube")
//public class YoutubeController {
//
//    @Autowired
//    private YoutubeService youtubeService;
//
//    @Operation(
//            summary = "Summarize YouTube video by URL",
//            description = "Provide a YouTube link and userId, returns summarized text with extracted keywords"
//    )
//    @PostMapping("/summarize")
//    public ResponseEntity<Summary> summarizeYoutubeVideo(
//            @Parameter(description = "YouTube video URL") @RequestParam String url,
//            @Parameter(description = "User ID of the person making the request") @RequestParam String userId
//    ) {
//        Summary summary = youtubeService.processYoutubeVideo(url, userId);
//        return ResponseEntity.ok(summary);
//    }
//}


//package com.summarizer.project.controller;
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.service.YoutubeService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@Tag(name = "YouTube Summarizer", description = "Extract captions and summarize YouTube video")
//@RestController
//@RequestMapping("/api/youtube")
//@CrossOrigin(origins = "*")
//public class YoutubeController {
//
//    @Autowired
//    private YoutubeService youtubeService;
//
//    @Operation(
//            summary = "Summarize YouTube video by URL",
//            description = "Provide a YouTube link and userId, returns summarized text with extracted keywords"
//    )
//    @PostMapping("/summarize")
//    public ResponseEntity<?> summarizeYoutubeVideo(
//            @Parameter(description = "YouTube video URL") @RequestParam String url,
//            @Parameter(description = "User ID of the person making the request") @RequestParam String userId
//    ) {
//        try {
//            Map<String, Object> response = (Map<String, Object>) youtubeService.processYoutubeVideo(url, userId);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body(Map.of("error", "Failed to process YouTube video: " + e.getMessage()));
//        }
//    }
//}

package com.summarizer.project.controller;

import com.summarizer.project.service.YoutubeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "YouTube Summarizer", description = "Extract captions and summarize YouTube video")
@RestController
@RequestMapping("/api/youtube")
//@CrossOrigin(origins = "*")
public class YoutubeController {

    @Autowired
    private YoutubeService youtubeService;

    @Operation(
            summary = "Summarize YouTube video by URL",
            description = "Provide a YouTube link and userId, returns summarized text with extracted keywords"
    )
    @PostMapping("/summarize")
    public ResponseEntity<?> summarizeYoutubeVideo(
            @Parameter(description = "YouTube video URL") @RequestParam String url,
            @Parameter(description = "User ID of the person making the request") @RequestParam String userId) {

        try {
            Map<String, Object> response = youtubeService.processYoutubeVideo(url, userId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid request",
                    "message", e.getMessage()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Processing failed",
                    "message", e.getMessage(),
                    "cause", e.getCause() != null ? e.getCause().getMessage() : "Unknown"
            ));
        }
    }
}
