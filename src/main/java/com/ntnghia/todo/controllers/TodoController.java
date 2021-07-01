package com.ntnghia.todo.controllers;

import com.ntnghia.todo.exceptions.NotFoundException;
import com.ntnghia.todo.models.Todo;
import com.ntnghia.todo.repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @GetMapping()
    List<Todo> getAll() {
        return todoRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Todo> get(@PathVariable int id) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);

        if (optionalTodo.isPresent()) {
            return optionalTodo;
        }

        throw new NotFoundException(String.format("Todo id %d not found", id));
    }

    @PostMapping()
    Todo post(@RequestBody Todo todo) {
        todo.setId(0);
        todoRepository.save(todo);

        return todo;
    }

    @PutMapping()
    void put(@RequestBody Todo todo) {
        todoRepository.save(todo);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable int id) {
        if (!todoRepository.existsById(id)) {
            throw new NotFoundException(String.format("Book id %d not found", id));
        }

        todoRepository.deleteById(id);
    }
}