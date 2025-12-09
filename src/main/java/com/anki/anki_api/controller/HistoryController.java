package com.anki.anki_api.controller;

import com.anki.anki_api.dto.HistoryRequest;
import com.anki.anki_api.entity.LearningHistory;
import com.anki.anki_api.service.HistoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/history")
public class HistoryController {
    @Autowired
    HistoryService historyService;

    @PostMapping
    public ResponseEntity<?> recordHistory(@Valid @RequestBody HistoryRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        
        LearningHistory history = historyService.recordHistory(request, currentPrincipalName);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Map<String, Object> stats = historyService.getStudentStats(currentPrincipalName);
        return ResponseEntity.ok(stats);
    }
}
