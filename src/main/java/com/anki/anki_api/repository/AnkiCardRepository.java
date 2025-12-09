package com.anki.anki_api.repository;

import com.anki.anki_api.entity.AnkiCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnkiCardRepository extends JpaRepository<AnkiCard, Long> {
}
