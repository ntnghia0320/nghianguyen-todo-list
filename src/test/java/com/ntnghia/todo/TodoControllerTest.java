package com.ntnghia.todo;

import com.google.gson.Gson;
import com.ntnghia.todo.models.Todo;
import com.ntnghia.todo.repositories.TodoRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
public class TodoControllerTest {
    @Autowired
    private TodoRepository todoRepository;

    private Todo todo1;
    private Todo todo2;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        todo1 = todoRepository.save(new Todo(1, "English"));
        todo2 = todoRepository.save(new Todo(2, "Mathematics"));
    }

    @AfterEach
    public void destroy() {
        todoRepository.deleteAll();
    }

    @Test
    public void test_getAllTodo() throws Exception {
        mockMvc.perform(get("/api/todo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(todo1.getId())))
                .andExpect(jsonPath("$[0].content", Matchers.equalTo("English")))
                .andExpect(jsonPath("$[1].id", Matchers.equalTo(todo2.getId())))
                .andExpect(jsonPath("$[1].content", Matchers.equalTo("Mathematics")));
    }

    @Test
    public void test_getBook_Found() throws Exception {
        mockMvc.perform(get("/api/todo/" + todo2.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(todo2.getId())))
                .andExpect(jsonPath("$.content", Matchers.equalTo(todo2.getContent())));
    }

    @Test
    public void test_getBook_NotFound() throws Exception {
        mockMvc.perform(get("/api/books/" + (todo1.getId() + todo2.getId())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_deleteBook_Found() throws Exception {
        mockMvc.perform(delete("/api/todo/" + todo1.getId()))
                .andExpect(status().isOk());

        assertFalse(todoRepository.findById(todo1.getId()).isPresent());
    }

    @Test
    public void test_deleteBook_NotFound() throws Exception {
        mockMvc.perform(delete("/api/books/" + (todo1.getId() + todo2.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_put_Found() throws Exception {

        Gson gson = new Gson();
        String json = gson.toJson(new Todo(todo2.getId(), "Math"));

        mockMvc.perform(put("/api/todo")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());

        Optional<Todo> book = todoRepository.findById(todo2.getId());

        assertTrue(book.isPresent());
        assertEquals(book.get().getContent(), "Math");
    }
}
