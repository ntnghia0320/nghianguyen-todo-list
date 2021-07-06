package com.ntnghia.task.service.impl;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.exception.BadRequestException;
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
        if (isIdExist(id)) return taskRepository.findById(id).get();

        throw new NotFoundException(String.format("Task id %d not found", id));
    }

    @Override
    public List<Task> findByKeyword(String keyword) {
        return taskRepository.findByTitleContainsOrDescriptionContains(keyword, keyword);
    }

    @Override
    public Task saveTask(Task task) throws NotFoundException {
        if (!isTaskExist(task)) {
            return taskRepository.save(task);
        }

        throw new BadRequestException("Task duplicate");
    }

    @Override
    public Task updateTask(int id, Task task) {
        if (isIdExist(id)) {
            Task taskOld = taskRepository.findById(id).get();
            if (taskOld.getTitle().equals(task.getTitle())
                    && taskOld.getDescription().equals(task.getDescription())
                    && taskOld.getDoneAt().equals(task.getDoneAt())) {
                throw new BadRequestException("Task not change");
            } else if (isTaskExist(task)) {
                throw new BadRequestException("Task duplicate");
            } else {
                task.setId(id);
                return taskRepository.save(task);
            }
        }

        throw new NotFoundException(String.format("Task id %d not found", id));
    }

    @Override
    public int deleteTask(int id) {
        if (isIdExist(id)) {
            taskRepository.deleteById(id);
            return id;
        }

        throw new NotFoundException(String.format("Task id %d not found", id));
    }

    private boolean isTaskExist(Task task) {
        return taskRepository.findByTitleAndDescription(task.getTitle(), task.getDescription()) != null;
    }

    private boolean isIdExist(int id) {
        return taskRepository.existsById(id);
    }
}
