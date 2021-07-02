package com.ntnghia.task.service.impl;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.exception.NotFoundException;
import com.ntnghia.task.repository.TaskRepository;
import com.ntnghia.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> findById(int id) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isPresent()) {
            return optionalTask;
        }

        throw new NotFoundException(String.format("Task id %d not found", id));
    }

    @Override
    public Optional<Task> findByTitle(String title) {
        Optional<Task> optionalTask = taskRepository.findByTitle(title);

        if (optionalTask.isPresent()) {
            return optionalTask;
        }

        throw new NotFoundException(String.format("Task title %s not found", title));
    }

    @Override
    public void saveTask(Task task) {
        task.setId(0);
        taskRepository.save(task);
    }

    @Override
    public void deleteTask(int id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException(String.format("Task id %d not found", id));
        }

        taskRepository.deleteById(id);
    }

}
