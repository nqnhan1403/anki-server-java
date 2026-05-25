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
@org.hibernate.annotations.SQLDelete(sql = "UPDATE anki_cards SET is_deleted = true WHERE id=?")
@org.hibernate.annotations.Where(clause = "is_deleted=false")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AnkiCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @NotBlank
    private String word;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WordType wordType = WordType.OTHER;

    private String pronunciation;

    private String definition;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    public AnkiCard(String word, WordType wordType, String pronunciation, String definition, Difficulty difficulty) {
        this.word = word;
        this.wordType = wordType != null ? wordType : WordType.OTHER;
        this.pronunciation = pronunciation;
        this.definition = definition;
        this.difficulty = difficulty;
    }
}
