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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
    private Task taskReturnExpected;
    private List<Task> listTaskReturnExpected;

    @BeforeEach
    public void beforeEach() {
        idExist = 2;
        idNotExist = 999;
        messageIdNotExist = "Task id 999 not found";
        taskReturnExpected = Task.builder().id(0).title("learn english").description("learn english word").build();
        listTaskReturnExpected = new ArrayList<>(Collections.singletonList(taskReturnExpected));

        when(taskRepositoryMock.existsById(idExist)).thenReturn(true);
    }

    @Test
    public void test_getAll() {
        when(taskRepositoryMock.findAll()).thenReturn(listTaskReturnExpected);

        List<Task> listTaskFoundActual = taskService.getAll();

        assertEquals(listTaskReturnExpected, listTaskFoundActual);
    }

    @Test
    public void test_findById() {
        when(taskRepositoryMock.findById(idExist)).thenReturn(java.util.Optional.of(taskReturnExpected));

        Task taskFoundActual = taskService.findById(idExist);

        assertThat(taskFoundActual).isEqualTo(taskReturnExpected);
    }

    @Test
    public void test_findById_Throw_Exception() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class, () -> taskService.findById(idNotExist), messageIdNotExist);

        assertTrue(thrown.getMessage().contains(messageIdNotExist));
    }

    @Test
    public void test_findByKeyword() {
        String keywordExist = "learn";
        when(taskRepositoryMock.findByTitleContainsOrDescriptionContains(keywordExist, keywordExist))
                .thenReturn(listTaskReturnExpected);

        List<Task> listTaskFoundActual = taskService.findByKeyword(keywordExist);

        assertThat(listTaskFoundActual).isEqualTo(listTaskReturnExpected);
    }

    @Test
    public void test_findByKeyword_NotFound() {
        List<Task> emptyList = new ArrayList<>();
        String keywordNotExist = "not_exist";

        when(taskRepositoryMock.findByTitleContainsOrDescriptionContains(keywordNotExist, keywordNotExist))
                .thenReturn(emptyList);

        List<Task> listTaskFound = taskService.findByKeyword(keywordNotExist);

        assertThat(listTaskFound).isEmpty();
    }

    @Test
    public void test_saveTask() {
        Task taskToSave = Task.builder().title("learn english").description("learn word").build();

        when(taskRepositoryMock.save(taskToSave)).thenReturn(taskReturnExpected);

        Task taskSaved = taskService.saveTask(taskToSave);

        assertEquals(taskReturnExpected, taskSaved);
    }

    @Test
    public void test_updateTask() {
        Task taskToUpdate = Task.builder().id(0).title("learn math").description("learn sum").build();

        when(taskRepositoryMock.save(taskToUpdate)).thenReturn(taskToUpdate);
        when(taskRepositoryMock.findByTitleAndDescription(anyString(), anyString())).thenReturn(null);
        when(taskRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(taskReturnExpected));

        Task taskUpdated = taskService.updateTask(idExist, taskToUpdate);

        assertEquals(taskToUpdate, taskUpdated);
    }

    @Test
    public void test_updateTask_Throw_Exception() {
        Task taskToUpdate = Task.builder().title("learn english").description("learn word").build();

        NotFoundException thrown = assertThrows(
                NotFoundException.class, () -> taskService.updateTask(idNotExist, taskToUpdate), messageIdNotExist);

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
                NotFoundException.class, () -> taskService.deleteTask(idNotExist), messageIdNotExist);

        assertTrue(thrown.getMessage().contains(messageIdNotExist));
    }

}
