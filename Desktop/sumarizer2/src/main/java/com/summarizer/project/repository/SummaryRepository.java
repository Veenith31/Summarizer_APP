package com.summarizer.project.repository;

import com.summarizer.project.model.Summary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;



public interface SummaryRepository extends MongoRepository<Summary, String> {
    List<Summary> findByUserId(String userId);

    //List<Summary> findByKeywordsContainingIgnoreCase( String userId,String keyword);
    Optional<Summary> findByUserIdAndOriginalText(String userId, String originalText);
    List<Summary> findByUserIdAndMode(String userId, String mode);



}