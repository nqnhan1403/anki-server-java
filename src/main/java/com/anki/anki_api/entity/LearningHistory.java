package com.anki.anki_api.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "learning_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private AnkiCard card;

    @Builder.Default
    private LocalDateTime reviewDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Difficulty rating;

    public LearningHistory(User student, AnkiCard card, Difficulty rating) {
        this.student = student;
        this.card = card;
        this.rating = rating;
        this.reviewDate = LocalDateTime.now();
    }
}
