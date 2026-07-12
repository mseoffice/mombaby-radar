package com.mombaby.radar.service;

import com.mombaby.radar.entity.AgentTask;
import com.mombaby.radar.repository.AgentTaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AgentTaskService {

    private final AgentTaskRepository agentTaskRepository;

    public AgentTaskService(AgentTaskRepository agentTaskRepository) {
        this.agentTaskRepository = agentTaskRepository;
    }

    public Page<AgentTask> list(Pageable pageable) {
        return agentTaskRepository.findAll(pageable);
    }

    public Optional<AgentTask> getById(Long id) {
        return agentTaskRepository.findById(id);
    }

    public AgentTask create(AgentTask task) {
        task.setId(null);
        task.setStatus("pending");
        task.setCreatedAt(LocalDateTime.now());
        return agentTaskRepository.save(task);
    }

    public AgentTask updateStatus(Long id, String status, Long executionTimeMs) {
        AgentTask task = agentTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent 任务不存在: " + id));
        task.setStatus(status);
        task.setExecutionTimeMs(executionTimeMs);
        if ("success".equals(status) || "failed".equals(status) || "partial".equals(status)) {
            task.setCompletedAt(LocalDateTime.now());
        }
        return agentTaskRepository.save(task);
    }
}
