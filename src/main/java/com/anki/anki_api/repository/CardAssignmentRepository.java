package com.anki.anki_api.repository;

import com.anki.anki_api.entity.CardAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardAssignmentRepository extends JpaRepository<CardAssignment, Long> {
    List<CardAssignment> findByStudentId(Long studentId);
    boolean existsByStudentIdAndCardId(Long studentId, Long cardId);
}
