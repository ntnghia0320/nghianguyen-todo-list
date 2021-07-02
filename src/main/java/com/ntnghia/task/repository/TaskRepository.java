package com.ntnghia.task.repository;

import com.ntnghia.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    Task findByTitle(String title);
}
