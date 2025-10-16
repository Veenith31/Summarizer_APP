//package com.summarizer.project.service;
//
//import com.summarizer.project.model.User;
//import com.summarizer.project.repository.UserRepository;
//import jakarta.mail.MessagingException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Random;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private EmailSenderService emailSenderService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    // === Signup ===
//    public String signUp(String email, String password) throws MessagingException {
//        if (userRepository.findByEmail(email) != null) {
//            return "Email already registered";
//        }
//
//        String verificationCode = String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit code
//        User newUser = new User(email, password, verificationCode);
//        newUser.setVerified(false);
//
//        userRepository.save(newUser);
//
//        // Send verification email
//        emailSenderService.sendEmail(email, "Verify Your Email", "Verification code: " + verificationCode);
//
//        return "Signup successful. Please verify your email.";
//    }
//
//    // === Verify Email ===
//    public String verifyEmail(String email, String code) {
//        User user = userRepository.findByEmail(email);
//        if (user != null && user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
//            user.setVerified(true);
//            user.setVerificationCode(null); // Clear code after successful verification
//            userRepository.save(user);
//            return "Email verified successfully!";
//        }
//        return "Invalid verification code.";
//    }
//
//    // === Login (basic, logic handled in controller) ===
//    public String login(String email, String password) {
//        return "Logged in successfully.";
//    }
//
//    // === Forgot Password ===
//    public String forgotPassword(String email) throws MessagingException {
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            return "User not found.";
//        }
//
//        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
//        user.setResetCode(code);
//        userRepository.save(user);
//
//        emailSenderService.sendEmail(email, "Password Reset Code",
//                "Use this code to reset your password: " + code);
//
//        return "Reset code sent to your email.";
//    }
//
//    // === Reset Password ===
//    public String resetPassword(String email, String code, String newPassword) {
//        User user = userRepository.findByEmail(email);
//        if (user != null && user.getResetCode() != null && user.getResetCode().equals(code)) {
//            user.setPassword(newPassword);
//            user.setResetCode(null); // Clear code after reset
//            userRepository.save(user);
//            return "Password reset successful!";
//        }
//        return "Invalid reset code.";
//    }
//}
package com.summarizer.project.service;

import com.summarizer.project.model.User;
import com.summarizer.project.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private UserRepository userRepository;

    // === Signup ===
    public String signUp(String email, String password) throws MessagingException {
        if (userRepository.findByEmail(email) != null) {
            return "Email already registered";
        }

        String verificationCode = String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit code
        User newUser = new User(email, password, verificationCode);
        newUser.setVerified(false);

        userRepository.save(newUser);

        // Send verification email
        emailSenderService.sendEmail(email, "Verify Your Email", "Verification code: " + verificationCode);

        return "Signup successful. Please verify your email.";
    }

    // === Verify Email ===
    public String verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
            user.setVerified(true);
            user.setVerificationCode(null); // Clear code after successful verification
            userRepository.save(user);
            return "Email verified successfully!";
        }
        return "Invalid verification code.";
    }

    // === Login (basic, logic handled in controller) ===
    public String login(String email, String password) {
        return "Logged in successfully.";
    }

    // === Forgot Password ===
    public String forgotPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "User not found.";
        }

        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        user.setResetCode(code);
        userRepository.save(user);

        emailSenderService.sendEmail(email, "Password Reset Code",
                "Use this code to reset your password: " + code);

        return "Reset code sent to your email.";
    }

    // === Reset Password ===
    public String resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getResetCode() != null && user.getResetCode().equals(code)) {
            user.setPassword(newPassword);
            user.setResetCode(null); // Clear code after reset
            userRepository.save(user);
            return "Password reset successful!";
        }
        return "Invalid reset code.";
    }
}
