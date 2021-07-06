package com.ntnghia.task.controller;

import com.ntnghia.task.entity.Task;
import com.ntnghia.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping()
    public List<Task> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    public Task getById(@PathVariable int id) {
        return taskService.findById(id);
    }

    @GetMapping("/search")
    public List<Task> getByKeyword(@RequestParam String keyword) {
        return taskService.findByKeyword(keyword);
    }

    @PostMapping()
    public Task post(@Valid @RequestBody Task task) {
        return taskService.saveTask(task);
    }

    @PutMapping("/{id}")
    public Task put(@PathVariable int id, @Valid @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public int delete(@PathVariable int id) {
        return taskService.deleteTask(id);
    }
}