package com.example.taskmanager;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) { this.repo = repo; }

    public Task create(TaskRequest r) {
        Task t = new Task();
        apply(t, r);
        return repo.save(t);
    }

    public List<Task> findAll() { return repo.findAllByOrderByCreatedAtDesc(); }

    public Task findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public List<Task> searchByTitle(String title) {
        return repo.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(title);
    }

    public Task update(Long id, TaskRequest r) {
        Task t = findById(id);
        apply(t, r);
        return repo.save(t);
    }

    public void delete(Long id) {
        repo.delete(findById(id));
    }

    private void apply(Task t, TaskRequest r) {
        t.setTitle(r.title().trim());
        t.setDescription(r.description());
        t.setStatus(r.status() != null ? r.status() : TaskStatus.TODO);
    }
}
