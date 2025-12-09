package com.anki.anki_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "card_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "card_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private AnkiCard card;

    @Builder.Default
    private LocalDateTime assignedAt = LocalDateTime.now();
    
    public CardAssignment(User student, AnkiCard card) {
        this.student = student;
        this.card = card;
        this.assignedAt = LocalDateTime.now();
    }
}
