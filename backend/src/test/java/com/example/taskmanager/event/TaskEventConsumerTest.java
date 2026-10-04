package com.example.taskmanager.event;

import com.example.taskmanager.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TaskEventConsumerTest {

    @Test
    void consume_processesAndStoresEvent() {
        TaskEventConsumer consumer = new TaskEventConsumer();
        TaskEvent event = new TaskEvent(TaskEventType.TaskCreated, 1L, "Consumer test task", TaskStatus.TODO, "Test details");

        consumer.consume(event);

        assertEquals(1, consumer.getProcessedEvents().size());
        TaskEvent received = consumer.getProcessedEvents().get(0);
        assertEquals(TaskEventType.TaskCreated, received.getEventType());
        assertEquals(1L, received.getTaskId());
        assertEquals("Consumer test task", received.getTitle());
    }
}
