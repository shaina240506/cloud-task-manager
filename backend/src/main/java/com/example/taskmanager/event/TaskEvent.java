package com.example.taskmanager.event;

import com.example.taskmanager.TaskStatus;
import java.time.Instant;

public class TaskEvent {
    private TaskEventType eventType;
    private Long taskId;
    private String title;
    private TaskStatus status;
    private Instant timestamp;
    private String details;

    public TaskEvent() {}

    public TaskEvent(TaskEventType eventType, Long taskId, String title, TaskStatus status, String details) {
        this.eventType = eventType;
        this.taskId = taskId;
        this.title = title;
        this.status = status;
        this.timestamp = Instant.now();
        this.details = details;
    }

    public TaskEventType getEventType() { return eventType; }
    public void setEventType(TaskEventType eventType) { this.eventType = eventType; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    @Override
    public String toString() {
        return "TaskEvent{" +
                "eventType=" + eventType +
                ", taskId=" + taskId +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", details='" + details + '\'' +
                '}';
    }
}
