package com.anki.anki_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "anki_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AnkiCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String word;

    private String pronunciation;

    private String definition;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    public AnkiCard(String word, String pronunciation, String definition, Difficulty difficulty) {
        this.word = word;
        this.pronunciation = pronunciation;
        this.definition = definition;
        this.difficulty = difficulty;
    }
}
