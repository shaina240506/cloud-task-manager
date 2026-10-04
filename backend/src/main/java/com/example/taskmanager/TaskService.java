package com.example.taskmanager;

import com.example.taskmanager.event.TaskEvent;
import com.example.taskmanager.event.TaskEventProducer;
import com.example.taskmanager.event.TaskEventType;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository repo;
    private final TaskEventProducer eventProducer;

    public TaskService(TaskRepository repo, TaskEventProducer eventProducer) {
        this.repo = repo;
        this.eventProducer = eventProducer;
    }

    public Task create(TaskRequest r) {
        Task t = new Task();
        apply(t, r);
        Task saved = repo.save(t);
        publishEvent(TaskEventType.TaskCreated, saved, "Task created via API");
        return saved;
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
        Task updated = repo.save(t);
        publishEvent(TaskEventType.TaskUpdated, updated, "Task updated via API");
        return updated;
    }

    public void delete(Long id) {
        Task t = findById(id);
        repo.delete(t);
        publishEvent(TaskEventType.TaskDeleted, t, "Task deleted via API");
    }

    private void apply(Task t, TaskRequest r) {
        t.setTitle(r.title().trim());
        t.setDescription(r.description());
        t.setStatus(r.status() != null ? r.status() : TaskStatus.TODO);
    }

    private void publishEvent(TaskEventType type, Task task, String details) {
        if (eventProducer != null) {
            TaskEvent event = new TaskEvent(type, task.getId(), task.getTitle(), task.getStatus(), details);
            eventProducer.sendEvent(event);
        }
    }
}

