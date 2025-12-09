package com.anki.anki_api.service;

import com.anki.anki_api.entity.AnkiCard;
import com.anki.anki_api.repository.AnkiCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {
    @Autowired
    AnkiCardRepository ankiCardRepository;

    public List<AnkiCard> getAllCards() {
        return ankiCardRepository.findAll();
    }

    public AnkiCard createCard(AnkiCard card) {
        return ankiCardRepository.save(card);
    }
}
