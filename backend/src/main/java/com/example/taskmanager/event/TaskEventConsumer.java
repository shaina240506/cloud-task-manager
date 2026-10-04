package com.example.taskmanager.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TaskEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TaskEventConsumer.class);
    private final List<TaskEvent> processedEvents = Collections.synchronizedList(new ArrayList<>());

    @KafkaListener(topics = "${app.kafka.topic:task-events}", groupId = "${spring.kafka.consumer.group-id:task-manager-group}")
    public void consume(TaskEvent event) {
        if (event == null) {
            log.warn("[KAFKA CONSUMER] Received null event");
            return;
        }

        log.info("[KAFKA CONSUMER] Received {} event: taskId={}", event.getEventType(), event.getTaskId());
        log.info("[KAFKA CONSUMER] Event Payload: title=\"{}\", status={}, timestamp={}, details=\"{}\"",
                event.getTitle(), event.getStatus(), event.getTimestamp(), event.getDetails());

        processedEvents.add(event);
    }

    public List<TaskEvent> getProcessedEvents() {
        return new ArrayList<>(processedEvents);
    }
}
