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

    @BeforeEach
    public void beforeEach() {
        idExist = 2;
        idNotExist = 999;
        messageIdNotExist = "Task id 999 not found";
    }

    @Test
    public void test_getAll() throws Exception {
        Task task = new Task(0, "learn english", "learn word");
        List<Task> listTaskReturn = new ArrayList<>(Collections.singletonList(task));

        when(taskService.getAll()).thenReturn(listTaskReturn);

        mockMvc.perform(get("/api/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(listTaskReturn.get(0).getId())))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo(listTaskReturn.get(0).getTitle())))
                .andExpect(jsonPath(
                        "$[0].description",
                        Matchers.equalTo(listTaskReturn.get(0).getDescription())
                ));
    }

    @Test
    public void test_getById_Found() throws Exception {
        Task taskFoundById = Task.builder().id(2).title("do homework").description("do exercise").build();

        when(taskService.findById(idExist)).thenReturn(taskFoundById);

        mockMvc.perform(get("/api/tasks/" + idExist))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(taskFoundById.getId())))
                .andExpect(jsonPath("$.title", Matchers.equalTo(taskFoundById.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.equalTo(taskFoundById.getDescription())));
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
        Task task = new Task(0, "learn english", "learn word");
        List<Task> listTaskExistKeyword = new ArrayList<>(Collections.singletonList(task));

        when(taskService.findByKeyword(keywordExist)).thenReturn(listTaskExistKeyword);

        mockMvc.perform(get("/api/tasks/keyword?keyword=" + keywordExist))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(listTaskExistKeyword.get(0).getId())))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo(listTaskExistKeyword.get(0).getTitle())))
                .andExpect(
                        jsonPath("$[0].description",
                                Matchers.equalTo(listTaskExistKeyword.get(0).getDescription()))
                );
    }

    @Test
    public void test_getByKeyword_NotFound() throws Exception {
        String keywordNotExist = "ooo";
        List<Task> emptyList = new ArrayList<>();

        when(taskService.findByKeyword(keywordNotExist)).thenReturn(emptyList);

        mockMvc.perform(get("/api/tasks/keyword?keyword=" + keywordNotExist))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void test_post() throws Exception {
        Task taskToPost = Task.builder().title("do homework").description("do exercise").build();
        Task taskReturn = Task.builder().id(2).title("do homework").description("do exercise").build();

        when(taskService.saveTask(taskToPost)).thenReturn(taskReturn);

        Gson gson = new Gson();
        String json = gson.toJson(taskToPost);

        mockMvc.perform(post("/api/tasks/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(taskReturn.getId())))
                .andExpect(jsonPath("$.title", Matchers.equalTo(taskReturn.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.equalTo(taskReturn.getDescription())));
    }

    @Test
    public void test_put_Found() throws Exception {
        Task taskToPut = Task.builder().title("do homework").description("do exercise").build();
        Task taskReturn = Task.builder().id(2).title("do homework").description("do exercise").build();

        when(taskService.updateTask(idExist, taskToPut)).thenReturn(taskReturn);

        Gson gson = new Gson();
        String json = gson.toJson(taskToPut);

        mockMvc.perform(put("/api/tasks/" + idExist)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(taskReturn.getId())))
                .andExpect(jsonPath("$.title", Matchers.equalTo(taskReturn.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.equalTo(taskReturn.getDescription())));
    }

    @Test
    public void test_put_NotFound() throws Exception {
        Task taskToPut = Task.builder().title("do homework").description("do exercise").build();

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
