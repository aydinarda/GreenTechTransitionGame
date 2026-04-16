package com.greentech.game.controller;

import com.greentech.game.domain.engine.ResetService;
import com.greentech.game.domain.model.GameSession;
import com.greentech.game.dto.request.ResetSessionRequest;
import com.greentech.game.dto.response.RoundResultResponse;
import com.greentech.game.dto.response.SessionResponse;
import com.greentech.game.exception.NotFoundException;
import com.greentech.game.exception.ValidationException;
import com.greentech.game.repository.GameSessionRepository;
import com.greentech.game.service.QueryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final GameSessionRepository sessionRepository;
    private final ResetService resetService;
    private final QueryService queryService;

    @Value("${game.admin-key:admin123}")
    private String adminKey;

    public AdminController(GameSessionRepository sessionRepository,
                           ResetService resetService,
                           QueryService queryService) {
        this.sessionRepository = sessionRepository;
        this.resetService = resetService;
        this.queryService = queryService;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        return ResponseEntity.ok(queryService.getAllSessions());
    }

    @GetMapping("/sessions/{code}/results")
    public ResponseEntity<List<RoundResultResponse>> getAllResults(@PathVariable String code) {
        return ResponseEntity.ok(queryService.getAllRoundResults(code));
    }

    @PostMapping("/sessions/{code}/reset")
    public ResponseEntity<SessionResponse> resetSession(
            @PathVariable String code,
            @Valid @RequestBody ResetSessionRequest req) {
        if (!adminKey.equals(req.adminKey())) {
            throw new ValidationException("Invalid admin key");
        }
        GameSession session = sessionRepository.findByCode(code)
            .orElseThrow(() -> new NotFoundException("Session not found: " + code));
        resetService.reset(session, req.resetType());
        return ResponseEntity.ok(SessionResponse.from(sessionRepository.save(session)));
    }
}
