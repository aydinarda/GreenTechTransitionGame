package com.greentech.game.controller;

import com.greentech.game.dto.request.CreateSessionRequest;
import com.greentech.game.dto.request.JoinSessionRequest;
import com.greentech.game.dto.response.PlayerStateResponse;
import com.greentech.game.dto.response.SessionResponse;
import com.greentech.game.service.QueryService;
import com.greentech.game.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final QueryService queryService;

    public SessionController(SessionService sessionService, QueryService queryService) {
        this.sessionService = sessionService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.createSession(req));
    }

    @PostMapping("/join")
    public ResponseEntity<SessionResponse> joinSession(@Valid @RequestBody JoinSessionRequest req) {
        return ResponseEntity.ok(sessionService.joinSession(req));
    }

    @GetMapping("/{code}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable String code) {
        return ResponseEntity.ok(sessionService.getSession(code));
    }

    @PostMapping("/{code}/start")
    public ResponseEntity<SessionResponse> startSession(@PathVariable String code) {
        return ResponseEntity.ok(sessionService.startSession(code));
    }

    @GetMapping("/{code}/leaderboard")
    public ResponseEntity<List<PlayerStateResponse>> getLeaderboard(@PathVariable String code) {
        return ResponseEntity.ok(queryService.getLeaderboard(code));
    }
}
