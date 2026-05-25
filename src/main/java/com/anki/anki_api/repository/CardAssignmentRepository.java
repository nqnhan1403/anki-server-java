package com.anki.anki_api.repository;

import com.anki.anki_api.entity.CardAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardAssignmentRepository extends JpaRepository<CardAssignment, Long> {
    List<CardAssignment> findByStudentId(Long studentId);
    org.springframework.data.domain.Page<CardAssignment> findByStudentId(Long studentId, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<CardAssignment> findByStudentIdAndNextReviewDateBefore(Long studentId, java.time.LocalDateTime date, org.springframework.data.domain.Pageable pageable);
    boolean existsByStudentIdAndCardId(Long studentId, Long cardId);
    java.util.Optional<CardAssignment> findByStudentIdAndCardId(Long studentId, Long cardId);
    void deleteByCardId(Long cardId);
}
