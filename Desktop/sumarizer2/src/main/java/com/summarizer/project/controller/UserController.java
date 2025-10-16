//package com.summarizer.project.controller;
//
//import com.summarizer.project.dto.*;
//import com.summarizer.project.model.User;
//import com.summarizer.project.repository.UserRepository;
//import com.summarizer.project.service.UserService;
//import jakarta.mail.MessagingException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/user")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @PostMapping("/signup")
//    public String signUp(@RequestBody SignUpRequest request) throws MessagingException {
//        return userService.signUp(request.getEmail(), request.getPassword());
//    }
//
//    @PostMapping("/verify")
//    public String verify(@RequestBody VerifyRequest request) {
//        return userService.verifyEmail(request.getEmail(), request.getCode());
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> payload) {
//        String email = payload.get("email");
//        String password = payload.get("password");
//
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//        }
//
//        if (!user.getPassword().equals(password)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
//        }
//
//        if (!user.isVerified()) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email not verified");
//        }
//
//        return ResponseEntity.ok(user.getEmail()); // ✅ Email used as userId
//    }
//
//    @PostMapping("/forgot-password")
//    public String forgotPassword(@RequestParam String email) throws MessagingException {
//        return userService.forgotPassword(email);
//    }
//
//    @PostMapping("/reset-password")
//    public String resetPassword(@RequestBody ResetPasswordRequest request) {
//        return userService.resetPassword(
//                request.getEmail(),
//                request.getCode(),
//                request.getNewPassword()
//        );
//    }
//}
//
package com.summarizer.project.controller;

import com.summarizer.project.dto.*;
import com.summarizer.project.model.User;
import com.summarizer.project.repository.UserRepository;
import com.summarizer.project.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public String signUp(@RequestBody SignUpRequest request) throws MessagingException {
        return userService.signUp(request.getEmail(), request.getPassword());
    }

    @PostMapping("/verify")
    public String verify(@RequestBody VerifyRequest request) {
        return userService.verifyEmail(request.getEmail(), request.getCode());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email not verified");
        }

        return ResponseEntity.ok(user.getEmail());
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) throws MessagingException {
        return userService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(
                request.getEmail(),
                request.getCode(),
                request.getNewPassword()
        );
    }
}
