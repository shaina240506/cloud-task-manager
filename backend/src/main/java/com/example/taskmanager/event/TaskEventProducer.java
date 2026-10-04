package com.example.taskmanager.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TaskEventProducer.class);

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;
    private final String topic;

    public TaskEventProducer(
            KafkaTemplate<String, TaskEvent> kafkaTemplate,
            @Value("${app.kafka.topic:task-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendEvent(TaskEvent event) {
        try {
            String key = event.getTaskId() != null ? String.valueOf(event.getTaskId()) : "0";
            kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA PRODUCER] Failed to publish {} event for taskId={}: {}",
                            event.getEventType(), event.getTaskId(), ex.getMessage());
                } else {
                    log.info("[KAFKA PRODUCER] Published {} event: taskId={}",
                            event.getEventType(), event.getTaskId());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA PRODUCER] Exception while publishing event: {}", e.getMessage(), e);
        }
    }
}
