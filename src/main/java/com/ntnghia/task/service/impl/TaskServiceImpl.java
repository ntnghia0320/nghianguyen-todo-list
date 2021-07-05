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
        if (taskRepository.findById(id).isPresent()) return taskRepository.findById(id).get();

        throw new NotFoundException(String.format("Task id %d not found", id));
    }

    @Override
    public List<Task> findByKeyword(String keyword) {
        if (!keyword.isEmpty()) return taskRepository.findByKeyword(keyword);

        throw new NotFoundException("Key word empty");
    }

    @Override
    public Task saveTask(Task task) {
        if (task.getTitle().isEmpty()) {
            throw new NotFoundException("Empty Title");
        } else if (taskRepository.findByTitleAndDescription(task.getTitle(), task.getDescription()) == null) {
            return taskRepository.save(task);
        }

        throw new NotFoundException("Task duplicate");
    }

    @Override
    public Task updateTask(int id, Task task) {
        if (taskRepository.existsById(id)) {
            Task taskOld = taskRepository.findById(id).get();
            if (task.getTitle().isEmpty()) {
                throw new NotFoundException("Empty Title");
            } else if (taskOld.getTitle().equals(task.getTitle()) || taskOld.getDescription().equals(task.getDescription())) {
                throw new NotFoundException("Task not change");
            } else if (taskRepository.findByTitleAndDescription(task.getTitle(), task.getDescription()) != null) {
                throw new NotFoundException("Task duplicate");
            } else {
                task.setId(id);
                return taskRepository.save(task);
            }
        }

        throw new NotFoundException(String.format("Task id %d not found", id));
    }

    @Override
    public int deleteTask(int id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return id;
        }

        throw new NotFoundException(String.format("Task id %d not found", id));
    }

}
