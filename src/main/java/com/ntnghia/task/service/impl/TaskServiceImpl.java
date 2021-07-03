package com.ntnghia.task.service.impl;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.exception.NotFoundException;
import com.ntnghia.task.repository.TaskRepository;
import com.ntnghia.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task findById(int id) {
        if (taskRepository.findById(id).isPresent()) {
            return taskRepository.findById(id).get();
        }

        throw new NotFoundException(String.format("Task id %d not found", id));
    }

    @Override
    public List<Task> findByKeyword(String keyword) {
            return taskRepository.findByKeyword(keyword);
    }

    @Override
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(int id, Task task) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException(String.format("Task id %d not found", id));
        }

        return taskRepository.save(task);
    }

    @Override
    public int deleteTask(int id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException(String.format("Task id %d not found", id));
        }

        taskRepository.deleteById(id);
        return id;
    }

}
