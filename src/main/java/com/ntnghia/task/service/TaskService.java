package com.ntnghia.task.service;

import com.ntnghia.task.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> getAll();

    Task saveTask(Task task);

    Task updateTask(int id, Task task);

    int deleteTask(int id);

    Task findById(int id);

    Task findByTitle(String title);
}
