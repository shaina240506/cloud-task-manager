package com.example.taskmanager.event;

import com.example.taskmanager.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskEventProducerTest {

    @Mock
    private KafkaTemplate<String, TaskEvent> kafkaTemplate;

    @Test
    void sendEvent_publishesEventToConfiguredTopic() {
        String topic = "test-task-events";
        TaskEventProducer producer = new TaskEventProducer(kafkaTemplate, topic);

        TaskEvent event = new TaskEvent(TaskEventType.TaskCreated, 100L, "Sample Task", TaskStatus.TODO, "Test metadata");

        when(kafkaTemplate.send(eq(topic), eq("100"), eq(event)))
                .thenReturn(CompletableFuture.completedFuture(null));

        producer.sendEvent(event);

        verify(kafkaTemplate).send(eq(topic), eq("100"), eq(event));
    }
}
