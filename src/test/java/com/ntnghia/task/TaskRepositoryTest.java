package com.ntnghia.task;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    public void beforeEach() {
        task = new Task(0, "learn java framework", "learn java spring");
    }

    @Test
    public void test_findByTitle() {
        taskRepository.save(task);

        assertNotNull(taskRepository.findByTitle("learn java framework"));
        assertNull(taskRepository.findByTitle("title abc"));
    }

    @AfterEach
    public void afterEach() {
        taskRepository.deleteById(task.getId());
    }
}
