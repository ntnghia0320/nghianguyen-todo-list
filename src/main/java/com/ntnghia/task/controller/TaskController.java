package com.ntnghia.task.controller;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    Task getById(@PathVariable int id) {
        return taskService.findById(id);
    }

    @GetMapping("/title/{title}")
    Task getByTitle(@PathVariable String title) {
        return taskService.findByTitle(title);
    }

    @PostMapping()
    Task post(@RequestBody Task task) {
        return taskService.saveTask(task);
    }

    @PutMapping("/{id}")
    Task put(@PathVariable int id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    int delete(@PathVariable int id) {
        return taskService.deleteTask(id);
    }
}