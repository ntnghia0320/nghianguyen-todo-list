package com.ntnghia.task;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.exception.NotFoundException;
import com.ntnghia.task.repository.TaskRepository;
import com.ntnghia.task.service.TaskService;
import com.ntnghia.task.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class TaskServiceImplTest {
    @TestConfiguration
    static class TaskServiceImplTestConfiguration {
        @Bean
        public TaskService taskService() {
            return new TaskServiceImpl();
        }
    }

    @Autowired
    private TaskServiceImpl taskServiceImpl;

    @MockBean
    private TaskRepository taskRepository;

    Task taskExpected;
    List<Task> taskListExpected;
    TaskServiceImpl taskServiceImplMock;

    @BeforeEach
    public void setUp() {
        taskExpected = new Task(0, "learn english", "learn word");
        taskListExpected = new ArrayList<>(Collections.singletonList(taskExpected));

        Mockito.when(taskRepository.findByTitle(taskExpected.getTitle()))
                .thenReturn(java.util.Optional.of(taskExpected));

        Mockito.when(taskRepository.findAll())
                .thenReturn(taskListExpected);

        Mockito.when(taskRepository.findById(taskExpected.getId()))
                .thenReturn(java.util.Optional.of(taskExpected));

        taskServiceImplMock = mock(TaskServiceImpl.class);
    }

    @Test
    public void test_getAll() {
        List<Task> taskListActual = taskServiceImpl.getAll();

        assertEquals(taskListExpected, taskListActual);
    }

    @Test
    public void test_findById() {
        int id = taskExpected.getId();
        Optional<Task> taskFound = taskServiceImpl.findById(id);

        assertThat(taskFound.get()).isEqualTo(taskExpected);
    }

    @Test
    public void test_findById_Throw_Exception() {
        int id = taskExpected.getId() + 9879;
        String message = String.format("Task id %d not found", id);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> taskServiceImpl.findById(id),
                message
        );

        assertTrue(thrown.getMessage().contains(message));
    }

    @Test
    public void test_findByTitle() {
        String title = "learn english";
        Optional<Task> taskFound = taskServiceImpl.findByTitle(title);

        assertThat(taskFound.get()).isEqualTo(taskExpected);
    }

    @Test
    public void test_findByTitle_Throw_Exception() {
        String title = "learn english not found";
        String message = String.format("Task title %s not found", title);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> taskServiceImpl.findByTitle(title),
                message
        );

        assertTrue(thrown.getMessage().contains(message));
    }

    @Test
    public void test_saveTask() {
        verify(taskServiceImplMock, times(0)).saveTask(taskExpected);
    }

    @Test
    public void test_deleteTask() {
        verify(taskServiceImplMock, times(0)).deleteTask(taskExpected.getId());
    }
}
