package com.anki.anki_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignmentRequest {
    @NotNull
    private Long cardId;
    @NotNull
    private Long studentId;
}
