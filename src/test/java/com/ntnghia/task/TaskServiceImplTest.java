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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

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
    private TaskRepository taskRepositoryMock;

    private Task taskExpected;
    private Task taskToUpdate;
    private List<Task> taskListExpected;

    @BeforeEach
    public void beforeEach() {
        taskExpected = new Task(0, "learn english", "learn word");
        taskToUpdate = new Task(0, "learn not_thing", "do not_thing");
        taskListExpected = new ArrayList<>(Collections.singletonList(taskExpected));

        Mockito.when(taskRepositoryMock.findAll())
                .thenReturn(taskListExpected);

        Mockito.when(taskRepositoryMock.findById(eq(taskExpected.getId())))
                .thenReturn(java.util.Optional.of(taskExpected));

        Mockito.when(taskRepositoryMock.findByKeyWord(eq(taskExpected.getTitle())))
                .thenReturn(taskExpected);

        Mockito.when(taskRepositoryMock.save(eq(taskExpected)))
                .thenReturn(taskExpected);

        Mockito.when(taskRepositoryMock.save(eq(taskToUpdate)))
                .thenReturn(taskToUpdate);

        Mockito.when(taskRepositoryMock.existsById(eq(taskExpected.getId())))
                .thenReturn(true);

        doNothing().when(taskRepositoryMock).deleteById(eq(taskExpected.getId()));
    }

    @Test
    public void test_getAll() {
        List<Task> taskListActual = taskServiceImpl.getAll();

        assertEquals(taskListExpected, taskListActual);
    }

    @Test
    public void test_findById() {
        Task taskFound = taskServiceImpl.findById(taskExpected.getId());

        assertThat(taskFound).isEqualTo(taskExpected);
    }

    @Test
    public void test_findById_Throw_Exception() {
        String message = String.format("Task id %d not found", 99999);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> taskServiceImpl.findById(99999),
                message
        );

        assertTrue(thrown.getMessage().contains(message));
    }

    @Test
    public void test_findByKeyWord() {
        String title = "learn english";
        Task taskFound = taskServiceImpl.findByKeyWord(title);

        assertThat(taskFound).isEqualTo(taskExpected);
    }

    @Test
    public void test_findByKeyWord_Throw_Exception() {
        String title = "XXXas";
        String message = String.format("Task key word %s not found", title);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> taskServiceImpl.findByKeyWord(title),
                message
        );

        assertTrue(thrown.getMessage().contains(message));
    }

    @Test
    public void test_saveTask() {
        Task taskSaved = taskServiceImpl.saveTask(taskExpected);

        assertEquals(taskExpected, taskSaved);
    }

    @Test
    public void test_updateTask() {
        int idTaskToUpdate = taskToUpdate.getId();
        Task taskUpdated = taskServiceImpl.updateTask(idTaskToUpdate, taskToUpdate);

        assertEquals(taskToUpdate, taskUpdated);
    }

    @Test
    public void test_updateTask_Throw_Exception() {
        String message = String.format("Task id %d not found", 9876);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> taskServiceImpl.updateTask(9876, taskToUpdate),
                message
        );

        assertTrue(thrown.getMessage().contains(message));
    }

    @Test
    public void test_deleteTask() {
        int idTaskToDelete = taskExpected.getId();
        int idTaskDeleted = taskServiceImpl.deleteTask(idTaskToDelete);

        assertEquals(idTaskToDelete, idTaskDeleted);
    }

    @Test
    public void test_deleteTask_Throw_Exception() {
        String message = String.format("Task id %d not found", 9876);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> taskServiceImpl.deleteTask(9876),
                message
        );

        assertTrue(thrown.getMessage().contains(message));
    }

}
