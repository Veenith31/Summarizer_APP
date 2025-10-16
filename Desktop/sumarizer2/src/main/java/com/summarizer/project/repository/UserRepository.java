package com.summarizer.project.repository;

import com.summarizer.project.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    User findByEmailAndVerificationCode(String email, String code);
    User findByEmailAndResetCode(String email, String resetCode);
}