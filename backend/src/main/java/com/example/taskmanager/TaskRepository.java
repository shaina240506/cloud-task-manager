package com.example.taskmanager;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);
    List<Task> findAllByOrderByCreatedAtDesc();
}
