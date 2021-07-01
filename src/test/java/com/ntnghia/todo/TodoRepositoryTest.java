package com.ntnghia.todo;

import com.ntnghia.todo.models.Todo;
import com.ntnghia.todo.repositories.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TodoRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void test_findByUsername() {
        todoRepository.save(new Todo(0, "content"));

        assertThat(todoRepository.findByContent("content")).isNotEmpty();
        assertThat(todoRepository.findByContent("content abc")).isEmpty();
    }
}
