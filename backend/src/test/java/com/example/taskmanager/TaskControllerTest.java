package com.example.taskmanager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired MockMvc mvc;
    @Autowired TaskRepository repository;
    @Autowired ObjectMapper mapper;

    @BeforeEach
    void clean() { repository.deleteAll(); }

    private long create(String title, TaskStatus status) throws Exception {
        String body = mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TaskRequest(title, "desc", status))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asLong();
    }

    @Test
    void createTask_returns201AndPersistsTask() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Write report\",\"description\":\"d\",\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Write report"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.createdAt").exists());
        org.junit.jupiter.api.Assertions.assertEquals(1, repository.count());
    }

    @Test
    void createTask_withBlankTitle_returns400() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"  \",\"status\":\"TODO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTasks_returnsAllCreatedTasks() throws Exception {
        create("One", TaskStatus.TODO);
        create("Two", TaskStatus.COMPLETED);
        mvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTaskById_returnsTask_andUnknownIdReturns404() throws Exception {
        long id = create("Find me", TaskStatus.TODO);
        mvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Find me"));
        mvc.perform(get("/api/tasks/999999")).andExpect(status().isNotFound());
    }

    @Test
    void updateTask_changesFields() throws Exception {
        long id = create("Old title", TaskStatus.TODO);
        mvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New title\",\"description\":\"new\",\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deleteTask_removesTask() throws Exception {
        long id = create("Delete me", TaskStatus.TODO);
        mvc.perform(delete("/api/tasks/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/tasks/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void searchByTitle_isCaseInsensitiveAndPartial() throws Exception {
        create("Buy groceries", TaskStatus.TODO);
        create("Pay rent", TaskStatus.TODO);
        mvc.perform(get("/api/tasks/search").param("title", "GROCER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Buy groceries"));
    }
}
