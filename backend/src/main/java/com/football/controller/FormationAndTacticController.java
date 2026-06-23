package com.football.controller;

import com.football.dto.FormationResponse;
import com.football.dto.TacticResponse;
import com.football.entity.Tactic;
import com.football.entity.TeamFormation;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.TacticRepository;
import com.football.repository.TeamFormationRepository;
import com.football.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
class FormationController {

    private final TeamFormationRepository formationRepository;
    private final MapperService mapper;

    @GetMapping
    public ResponseEntity<List<FormationResponse>> getAll() {
        return ResponseEntity.ok(formationRepository.findAll().stream()
                .map(mapper::toFormationResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormationResponse> getById(@PathVariable Long id) {
        TeamFormation f = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found"));
        return ResponseEntity.ok(mapper.toFormationResponse(f));
    }
}

@RestController
@RequestMapping("/api/tactics")
@RequiredArgsConstructor
class TacticController {

    private final TacticRepository tacticRepository;
    private final MapperService mapper;

    @GetMapping
    public ResponseEntity<List<TacticResponse>> getAll() {
        return ResponseEntity.ok(tacticRepository.findAll().stream()
                .map(mapper::toTacticResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TacticResponse> getById(@PathVariable Long id) {
        Tactic t = tacticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tactic not found"));
        return ResponseEntity.ok(mapper.toTacticResponse(t));
    }
}
