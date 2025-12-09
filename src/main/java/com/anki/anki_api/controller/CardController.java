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
}
