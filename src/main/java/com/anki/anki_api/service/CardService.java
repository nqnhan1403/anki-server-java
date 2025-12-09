package com.anki.anki_api.service;

import com.anki.anki_api.entity.AnkiCard;
import com.anki.anki_api.repository.AnkiCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@org.springframework.transaction.annotation.Transactional
public class CardService {
    @Autowired
    AnkiCardRepository ankiCardRepository;

    @Autowired
    com.anki.anki_api.repository.CardAssignmentRepository cardAssignmentRepository;

    @Autowired
    com.anki.anki_api.repository.UserRepository userRepository;

    public List<AnkiCard> getAllCards() {
        return ankiCardRepository.findAll();
    }

    public AnkiCard createCard(AnkiCard card) {
        return ankiCardRepository.save(card);
    }

    public void assignCardToStudent(Long cardId, Long studentId) {
        if (cardAssignmentRepository.existsByStudentIdAndCardId(studentId, cardId)) {
            return; // Already assigned
        }

        AnkiCard card = ankiCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        com.anki.anki_api.entity.User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        com.anki.anki_api.entity.CardAssignment assignment = new com.anki.anki_api.entity.CardAssignment(student, card);
        cardAssignmentRepository.save(assignment);
    }

    public List<AnkiCard> getAssignedCards(Long studentId) {
        return cardAssignmentRepository.findByStudentId(studentId).stream()
                .map(com.anki.anki_api.entity.CardAssignment::getCard)
                .collect(java.util.stream.Collectors.toList());
    }
}
