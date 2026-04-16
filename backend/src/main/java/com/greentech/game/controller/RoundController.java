package com.greentech.game.controller;

import com.greentech.game.dto.request.SubmitActionRequest;
import com.greentech.game.dto.response.RoundResponse;
import com.greentech.game.dto.response.RoundResultResponse;
import com.greentech.game.service.RoundService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions/{code}/rounds")
public class RoundController {

    private final RoundService roundService;

    public RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    @PostMapping
    public ResponseEntity<RoundResponse> openRound(@PathVariable String code) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roundService.openRound(code));
    }

    @GetMapping
    public ResponseEntity<List<RoundResponse>> getRounds(@PathVariable String code) {
        return ResponseEntity.ok(roundService.getRounds(code));
    }

    @PostMapping("/{roundId}/actions")
    public ResponseEntity<RoundResponse> submitAction(
            @PathVariable String code,
            @PathVariable Long roundId,
            @Valid @RequestBody SubmitActionRequest req) {
        return ResponseEntity.ok(roundService.submitAction(code, roundId, req));
    }

    @PostMapping("/{roundId}/resolve")
    public ResponseEntity<RoundResultResponse> resolveRound(
            @PathVariable String code,
            @PathVariable Long roundId) {
        return ResponseEntity.ok(roundService.resolveRound(code, roundId));
    }

    @GetMapping("/{roundId}/result")
    public ResponseEntity<RoundResultResponse> getRoundResult(
            @PathVariable String code,
            @PathVariable Long roundId) {
        return ResponseEntity.ok(roundService.getRoundResult(code, roundId));
    }
}
