package com.anki.anki_api.controller;

import com.anki.anki_api.entity.AnkiCard;
import com.anki.anki_api.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    CardService cardService;

    @Autowired
    com.anki.anki_api.repository.UserRepository userRepository;

    @GetMapping
    public org.springframework.data.domain.Page<AnkiCard> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return cardService.getAllCards(org.springframework.data.domain.PageRequest.of(page, size));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createCard(@Valid @RequestBody AnkiCard card) {
        AnkiCard createdCard = cardService.createCard(card);
        return ResponseEntity.ok(createdCard);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> assignCard(@Valid @RequestBody com.anki.anki_api.dto.AssignmentRequest request) {
        cardService.assignCardToStudent(request.getCardId(), request.getStudentId());
        return ResponseEntity.ok("Card assigned successfully!");
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public org.springframework.data.domain.Page<AnkiCard> getAssignedCards(
            org.springframework.security.core.Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = authentication.getName();
        com.anki.anki_api.entity.User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
        return cardService.getAssignedCards(user.getId(), org.springframework.data.domain.PageRequest.of(page, size));
    }

    @GetMapping("/due")
    @PreAuthorize("hasRole('STUDENT')")
    public org.springframework.data.domain.Page<AnkiCard> getDueCards(
            org.springframework.security.core.Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = authentication.getName();
        com.anki.anki_api.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cardService.getDueCards(user.getId(), org.springframework.data.domain.PageRequest.of(page, size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateCard(@PathVariable Long id, @Valid @RequestBody AnkiCard card) {
        card.setId(id);
        AnkiCard updatedCard = cardService.createCard(card);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok().build();
    }
}
