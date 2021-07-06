package com.ntnghia.task;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    public void beforeEach() {
        task = Task.builder().title("learn english").description("learn english word").build();
        task = taskRepository.save(task);
    }

    @Test
    public void test_findByTitleContainsOrDescriptionContains() {
        assertTrue(
                taskRepository.findByTitleContainsOrDescriptionContains(
                        "xxxx",
                        "yyy").isEmpty());

        assertFalse(
                taskRepository.findByTitleContainsOrDescriptionContains(
                        "learn english",
                        "yyy").isEmpty());
    }

    @Test
    public void test_findByTitleAndDescription() {
        assertEquals(taskRepository.findByTitleAndDescription(
                "learn english",
                "learn english word"), task);

        assertNotEquals(taskRepository.findByTitleAndDescription(
                "learn english 2",
                "learn english word"), task);
    }

    @AfterEach
    public void afterEach() {
        taskRepository.deleteById(task.getId());
    }
}
