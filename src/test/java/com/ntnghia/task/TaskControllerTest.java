package com.ntnghia.task;

import com.google.gson.Gson;
import com.ntnghia.task.entity.Task;
import com.ntnghia.task.service.impl.TaskServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class TaskControllerTest {
    @Autowired
    private TaskServiceImpl taskServiceImpl;

    private Task task1;
    private Task task2;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        task1 = new Task(0, "learn java framework", "learn java spring");
        task2 = new Task(0, "learn english", "learn english word");
        taskServiceImpl.saveTask(task1);
        taskServiceImpl.saveTask(task2);
    }

    @AfterEach
    public void destroy() {
        taskServiceImpl.deleteTask(task1.getId());
        taskServiceImpl.deleteTask(task2.getId());
    }

    @Test
    public void test_getAll() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(task1.getId())))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("learn java framework")))
                .andExpect(jsonPath("$[0].description", Matchers.equalTo("learn java spring")))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(task2.getId())))
                .andExpect(jsonPath("$[1].title", Matchers.equalTo("learn english")))
                .andExpect(jsonPath("$[1].description", Matchers.equalTo("learn english word")));
    }

    @Test
    public void test_getById_Found() throws Exception {
        mockMvc.perform(get("/api/tasks/" + task1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(task1.getId())))
                .andExpect(jsonPath("$.title", Matchers.equalTo(task1.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.equalTo(task1.getDescription())));
    }

    @Test
    public void test_getById_NotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_getByKeyword_Found() throws Exception {
        mockMvc.perform(get("/api/tasks/keyword?keyword=learn"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(task1.getId())))
                .andExpect(jsonPath("$[0].title", Matchers.equalTo("learn java framework")))
                .andExpect(jsonPath("$[0].description", Matchers.equalTo("learn java spring")))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(task2.getId())))
                .andExpect(jsonPath("$[1].title", Matchers.equalTo("learn english")))
                .andExpect(jsonPath("$[1].description", Matchers.equalTo("learn english word")));
    }

    @Test
    public void test_getByKeyword_NotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/keyword?keyword=not_exist"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Nested
    class TestPost {
        Task task = new Task(0, "do homework", "do exercise");

        @Test
        public void test_post() throws Exception {
            Gson gson = new Gson();
            String json = gson.toJson(task);

            mockMvc.perform(post("/api/tasks/")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.title", Matchers.equalTo("do homework")))
                    .andExpect(jsonPath("$.description", Matchers.equalTo("do exercise")));
        }

        @AfterEach
        public void afterEach() {
            taskServiceImpl.deleteTask(taskServiceImpl.findByKeyword("do homework").get(0).getId());
        }
    }

    @Test
    public void test_put_Found() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(new Task(task1.getId(), "learn math", "learn sum sub"));

        mockMvc.perform(put("/api/tasks/" + task1.getId())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(task1.getId())))
                .andExpect(jsonPath("$.title", Matchers.equalTo("learn math")))
                .andExpect(jsonPath("$.description", Matchers.equalTo("learn sum sub")));
    }

    @Test
    public void test_put_NotFound() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(new Task(task1.getId(), "learn math", "learn sum sub"));

        mockMvc.perform(put("/api/tasks/" + 99999)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Nested
    class TestDelete {
        Task taskToDelete;

        @BeforeEach
        public void beforeEach() {
            taskToDelete = new Task(0, "task to delete", "not thing");
            taskServiceImpl.saveTask(taskToDelete);
            taskToDelete.setId(taskServiceImpl.findByKeyword("task to delete").get(0).getId());
        }

        @Test
        public void test_delete_Found() throws Exception {
            mockMvc.perform(delete("/api/tasks/" + taskToDelete.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(String.valueOf(taskToDelete.getId())));
        }

        @Test
        public void test_delete_NotFound() throws Exception {
            mockMvc.perform(delete("/api/tasks/9999999"))
                    .andExpect(status().isNotFound());
        }
    }

}
