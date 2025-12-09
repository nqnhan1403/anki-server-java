package com.anki.anki_api.service;

import com.anki.anki_api.dto.HistoryRequest;
import com.anki.anki_api.entity.AnkiCard;
import com.anki.anki_api.entity.LearningHistory;
import com.anki.anki_api.entity.User;
import com.anki.anki_api.repository.AnkiCardRepository;
import com.anki.anki_api.repository.LearningHistoryRepository;
import com.anki.anki_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class HistoryService {
    @Autowired
    LearningHistoryRepository historyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AnkiCardRepository cardRepository;

    @Transactional
    public LearningHistory recordHistory(HistoryRequest request, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        AnkiCard card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + request.getCardId()));

        LearningHistory history = LearningHistory.builder()
                .student(student)
                .card(card)
                .rating(request.getRating())
                .build();
        return historyRepository.save(history);
    }

    public Map<String, Object> getStudentStats(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Long learnedCount = historyRepository.countDistinctLearnedCardsByStudentId(student.getId());

        Map<String, Object> stats = new HashMap<>();
        stats.put("learnedCardsCount", learnedCount);
        
        // "the last time they feel about hard level" -> Get last record with HARD rating?
        // historyRepository.findTopByStudentIdAndRatingOrderByReviewDateDesc(student.getId(), Difficulty.HARD);
        
        return stats;
    }
}
