package com.anki.anki_api.repository;

import com.anki.anki_api.entity.Difficulty;
import com.anki.anki_api.entity.LearningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LearningHistoryRepository extends JpaRepository<LearningHistory, Long> {
    
    @Query("SELECT COUNT(DISTINCT h.card.id) FROM LearningHistory h WHERE h.student.id = :studentId")
    Long countDistinctLearnedCardsByStudentId(@Param("studentId") Long studentId);

    // Find the latest history entry for a student with a specific rating
    Optional<LearningHistory> findTopByStudentIdAndRatingOrderByReviewDateDesc(Long studentId, Difficulty rating);
}
