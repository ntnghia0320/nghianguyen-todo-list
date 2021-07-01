package com.ntnghia.todo.repositories;

import com.ntnghia.todo.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    Optional<Todo> findByContent(String content);
}
