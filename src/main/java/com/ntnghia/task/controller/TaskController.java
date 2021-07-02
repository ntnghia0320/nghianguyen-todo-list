package com.ntnghia.task.controller;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping()
    List<Task> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    Optional<Task> getById(@PathVariable int id) {
        return taskService.findById(id);
    }

    @GetMapping("/title/{title}")
    Optional<Task> getByTitle(@PathVariable String title) {
        return taskService.findByTitle(title);
    }

    @PostMapping()
    void post(@RequestBody Task task) {
        taskService.saveTask(task);
    }

    @PutMapping()
    void put(@RequestBody Task task) {
        taskService.saveTask(task);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable int id) {
        taskService.deleteTask(id);
    }
}