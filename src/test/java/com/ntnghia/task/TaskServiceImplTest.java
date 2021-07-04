package com.ntnghia.task;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.exception.NotFoundException;
import com.ntnghia.task.repository.TaskRepository;
import com.ntnghia.task.service.TaskService;
import com.ntnghia.task.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
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
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepositoryMock;

    private int idExist;
    private int idNotExist;
    private String messageIdNotExist;
    private Task taskFoundExpected;
    private List<Task> listTaskFoundExpected;

    @BeforeEach
    public void beforeEach() {
        idExist = 2;
        idNotExist = 999;
        messageIdNotExist = "Task id 999 not found";
        taskFoundExpected = new Task(0, "learn english", "learn word");
        listTaskFoundExpected = new ArrayList<>(Collections.singletonList(taskFoundExpected));

        when(taskRepositoryMock.existsById(idExist))
                .thenReturn(true);
    }

    @Test
    public void test_getAll() {
        when(taskRepositoryMock.findAll()).thenReturn(listTaskFoundExpected);

        List<Task> listTaskFoundActual = taskService.getAll();

        assertEquals(listTaskFoundExpected, listTaskFoundActual);
    }

    @Test
    public void test_findById() {
        when(taskRepositoryMock.findById(idExist)).thenReturn(java.util.Optional.of(taskFoundExpected));

        Task taskFoundActual = taskService.findById(idExist);

        assertThat(taskFoundActual).isEqualTo(taskFoundExpected);
    }

    @Test
    public void test_findById_Throw_Exception() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> taskService.findById(idNotExist),
                messageIdNotExist
        );

        assertTrue(thrown.getMessage().contains(messageIdNotExist));
    }

    @Test
    public void test_findByKeyword() {
        String keywordExist = "learn";
        when(taskRepositoryMock.findByKeyword(keywordExist)).thenReturn(listTaskFoundExpected);

        List<Task> listTaskFoundActual = taskService.findByKeyword(keywordExist);

        assertThat(listTaskFoundActual).isEqualTo(listTaskFoundExpected);
    }

    @Test
    public void test_findByKeyword_NotFound() {
        List<Task> emptyList = new ArrayList<>();
        String keywordNotExist = "not_exist";

        when(taskRepositoryMock.findByKeyword(keywordNotExist))
                .thenReturn(emptyList);

        List<Task> listTaskFound = taskService.findByKeyword(keywordNotExist);

        assertThat(listTaskFound).isEmpty();
    }

    @Test
    public void test_saveTask() {
        Task taskToSave = Task.builder().title("learn abc").description("do abc").build();
        Task taskReturn = new Task(1, "learn abc", "do abc");

        when(taskRepositoryMock.save(taskToSave)).thenReturn(taskReturn);

        Task taskSaved = taskService.saveTask(taskToSave);

        assertEquals(taskReturn, taskSaved);
    }

    @Test
    public void test_updateTask() {
        Task taskToUpdate = Task.builder().title("learn abc").description("do abc").build();
        Task taskReturn = new Task(idExist, "learn abc", "do abc");

        when(taskRepositoryMock.save(taskToUpdate)).thenReturn(taskReturn);

        Task taskUpdated = taskService.updateTask(idExist, taskToUpdate);

        assertEquals(taskToUpdate, taskUpdated);
    }

    @Test
    public void test_updateTask_Throw_Exception() {
        Task taskToUpdate = Task.builder().title("learn abc").description("do abc").build();

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> taskService.updateTask(idNotExist, taskToUpdate),
                messageIdNotExist
        );

        assertTrue(thrown.getMessage().contains(messageIdNotExist));
    }

    @Test
    public void test_deleteTask() {
        doNothing().when(taskRepositoryMock).deleteById(idExist);

        int idTaskDeleted = taskService.deleteTask(idExist);

        assertEquals(idExist, idTaskDeleted);
    }

    @Test
    public void test_deleteTask_Throw_Exception() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> taskService.deleteTask(idNotExist),
                messageIdNotExist
        );

        assertTrue(thrown.getMessage().contains(messageIdNotExist));
    }

}
