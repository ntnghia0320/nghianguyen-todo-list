package com.ntnghia.task.service;

import com.ntnghia.task.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> getAll();

    void saveTask(Task task);

    void deleteTask(int id);

    Optional<Task> findById(int id);

    Optional<Task> findByTitle(String title);
}
