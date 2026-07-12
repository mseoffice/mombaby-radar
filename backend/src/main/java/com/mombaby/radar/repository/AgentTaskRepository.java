package com.mombaby.radar.repository;

import com.mombaby.radar.entity.AgentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AgentTaskRepository extends JpaRepository<AgentTask, Long> {

    List<AgentTask> findByAgentType(String agentType);

    List<AgentTask> findByStatus(String status);
}
