package com.summarizer.project.controller;

import com.summarizer.project.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class EmailTestController {

    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping("/send-email")
    public String sendTestEmail(@RequestParam String toEmail) {
        try {
            String subject = "Test Email from TheSummarizer";
            String body = "This is a test email and your code is 975231";
            emailSenderService.sendEmail(toEmail, subject, body);
            return "Test email sent to " + toEmail;
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }
}