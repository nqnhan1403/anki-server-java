package com.anki.anki_api.controller;

import com.anki.anki_api.entity.AnkiCard;
import com.anki.anki_api.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    CardService cardService;

    @Autowired
    com.anki.anki_api.repository.UserRepository userRepository;

    @GetMapping
    public List<AnkiCard> getAllCards() {
        return cardService.getAllCards();
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
    public List<AnkiCard> getAssignedCards(org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        com.anki.anki_api.entity.User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("USERRR: " + user);
        return cardService.getAssignedCards(user.getId());
    }
}
