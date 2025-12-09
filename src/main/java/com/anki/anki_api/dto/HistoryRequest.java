package com.anki.anki_api.dto;

import com.anki.anki_api.entity.Difficulty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRequest {
    @NotNull
    private Long cardId;

    @NotNull
    private Difficulty rating;
}
