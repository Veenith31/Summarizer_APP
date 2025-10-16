//package com.summarizer.project.controller;
//
//import com.summarizer.project.model.Summary;
//import com.summarizer.project.service.OcrService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/api/ocr")
//public class OcrController {
//
//    @Autowired
//    private OcrService ocrService;
//
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Summary> uploadImageAndSummarize(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("userId") String userId) {
//
//        Summary summary = ocrService.processImage(file, userId);
//        return ResponseEntity.ok(summary);
//    }
//}


package com.summarizer.project.controller;

import com.summarizer.project.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/ocr")
//@CrossOrigin(origins = "*")
public class OcrController {

    @Autowired
    private OcrService ocrService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImageAndSummarize(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId) {

        try {
            Map<String, Object> result = (Map<String, Object>) ocrService.processImage(file, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
