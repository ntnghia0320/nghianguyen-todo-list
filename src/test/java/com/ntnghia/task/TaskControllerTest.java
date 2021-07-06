package com.ntnghia.task;

import com.google.gson.Gson;
import com.ntnghia.task.entity.Task;
import com.ntnghia.task.exception.NotFoundException;
import com.ntnghia.task.service.TaskService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class TaskControllerTest {
    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    private int idExist;
    private int idNotExist;
    private String messageIdNotExist;
    private Task taskReturnExpected;
    private List<Task> listTaskReturnExpected;

    @BeforeEach
    public void beforeEach() {
        idExist = 2;
        idNotExist = 999;
        messageIdNotExist = "Task id 999 not found";
        taskReturnExpected = Task.builder().id(0).title("learn english").description("learn english word").build();
        listTaskReturnExpected = new ArrayList<>(Collections.singletonList(taskReturnExpected));
    }

    @Test
    public void test_getAll() throws Exception {
        when(taskService.getAll()).thenReturn(listTaskReturnExpected);

        mockMvc.perform(get("/api/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(listTaskReturnExpected.get(0).getId())))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo(listTaskReturnExpected.get(0).getTitle())))
                .andExpect(jsonPath(
                        "$[0].description",
                        Matchers.equalTo(listTaskReturnExpected.get(0).getDescription())
                ));
    }

    @Test
    public void test_getById_Found() throws Exception {
        when(taskService.findById(idExist)).thenReturn(taskReturnExpected);

        mockMvc.perform(get("/api/tasks/" + idExist))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(taskReturnExpected.getId())))
                .andExpect(jsonPath("$.title", Matchers.equalTo(taskReturnExpected.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.equalTo(taskReturnExpected.getDescription())));
    }

    @Test
    public void test_getById_NotFound() throws Exception {
        when(taskService.findById(idNotExist)).thenThrow(new NotFoundException(messageIdNotExist));

        mockMvc.perform(get("/api/tasks/" + idNotExist))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals(messageIdNotExist, result.getResolvedException().getMessage()));
    }

    @Test
    public void test_getByKeyword_Found() throws Exception {
        String keywordExist = "learn";

        when(taskService.findByKeyword(keywordExist)).thenReturn(listTaskReturnExpected);

        mockMvc.perform(get("/api/tasks/search?keyword=" + keywordExist))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(listTaskReturnExpected.get(0).getId())))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo(listTaskReturnExpected.get(0).getTitle())))
                .andExpect(
                        jsonPath("$[0].description",
                                Matchers.equalTo(listTaskReturnExpected.get(0).getDescription()))
                );
    }

    @Test
    public void test_getByKeyword_NotFound() throws Exception {
        String keywordNotExist = "ooo";
        List<Task> emptyList = new ArrayList<>();

        when(taskService.findByKeyword(keywordNotExist)).thenReturn(emptyList);

        mockMvc.perform(get("/api/tasks/search?keyword=" + keywordNotExist))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void test_post() throws Exception {
        Task taskToPost = Task.builder().title("learn english").description("learn word").build();

        when(taskService.saveTask(taskToPost)).thenReturn(taskReturnExpected);

        Gson gson = new Gson();
        String json = gson.toJson(taskToPost);

        mockMvc.perform(post("/api/tasks/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(taskReturnExpected.getId())))
                .andExpect(jsonPath("$.title", Matchers.equalTo(taskReturnExpected.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.equalTo(taskReturnExpected.getDescription())));
    }

    @Test
    public void test_put_Found() throws Exception {
        Task taskToPut = Task.builder().title("learn english").description("learn word").build();

        when(taskService.updateTask(idExist, taskToPut)).thenReturn(taskReturnExpected);

        Gson gson = new Gson();
        String json = gson.toJson(taskToPut);

        mockMvc.perform(put("/api/tasks/" + idExist)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(taskReturnExpected.getId())))
                .andExpect(jsonPath("$.title", Matchers.equalTo(taskReturnExpected.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.equalTo(taskReturnExpected.getDescription())));
    }

    @Test
    public void test_put_NotFound() throws Exception {
        Task taskToPut = Task.builder().title("learn english").description("learn word").build();

        when(taskService.updateTask(idNotExist, taskToPut)).thenThrow(new NotFoundException(messageIdNotExist));

        Gson gson = new Gson();
        String json = gson.toJson(taskToPut);

        mockMvc.perform(put("/api/tasks/" + idNotExist)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals(messageIdNotExist, result.getResolvedException().getMessage()));
    }

    @Test
    public void test_delete_Found() throws Exception {
        when(taskService.deleteTask(idExist)).thenReturn(idExist);

        mockMvc.perform(delete("/api/tasks/" + idExist))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(idExist)));
    }

    @Test
    public void test_delete_NotFound() throws Exception {
        when(taskService.deleteTask(idNotExist)).thenThrow(new NotFoundException(messageIdNotExist));

        mockMvc.perform(delete("/api/tasks/" + idNotExist))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals(messageIdNotExist, result.getResolvedException().getMessage()));
    }

}
