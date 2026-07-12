package com.mombaby.radar.controller;

import com.mombaby.radar.entity.AgentTask;
import com.mombaby.radar.service.AgentTaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agent-tasks")
public class AgentTaskController {

    private final AgentTaskService agentTaskService;

    public AgentTaskController(AgentTaskService agentTaskService) {
        this.agentTaskService = agentTaskService;
    }

    @GetMapping
    public ResponseEntity<Page<AgentTask>> list(Pageable pageable) {
        return ResponseEntity.ok(agentTaskService.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentTask> get(@PathVariable Long id) {
        return agentTaskService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AgentTask> create(@RequestBody AgentTask task) {
        return ResponseEntity.ok(agentTaskService.create(task));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AgentTask> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Long executionTimeMs) {
        return ResponseEntity.ok(agentTaskService.updateStatus(id, status, executionTimeMs));
    }
}
