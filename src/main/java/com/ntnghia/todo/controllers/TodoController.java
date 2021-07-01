package com.ntnghia.todo.controllers;

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
    List<Todo> getAll(){
        return todoRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Todo> get(@PathVariable int id){
        return todoRepository.findById(id);
    }

    @PostMapping()
    Todo post(@RequestBody Todo todo){
        todo.setId(0);
        todoRepository.save(todo);

        return todo;
    }

    @PutMapping()
    void put(@RequestBody Todo todo){
        todoRepository.save(todo);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable int id){
        todoRepository.deleteById(id);
    }
}