package com.summarizer.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String email;
    private String password;
    private boolean verified;
    private String verificationCode;
    private String resetCode;
    private LocalDateTime lastActive;

    // Constructors
    public User() {}

    public User(String email, String password, String verificationCode) {
        this.email = email;
        this.password = password;
        this.verificationCode = verificationCode;
        this.verified = false;
        this.lastActive = LocalDateTime.now();
    }

    // Getters and Setters
    // (Generate using your IDE or manually add them here)

    // Example:
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public String getResetCode() { return resetCode; }
    public void setResetCode(String resetCode) { this.resetCode = resetCode; }

    public LocalDateTime getLastActive() { return lastActive; }
    public void setLastActive(LocalDateTime lastActive) { this.lastActive = lastActive; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}