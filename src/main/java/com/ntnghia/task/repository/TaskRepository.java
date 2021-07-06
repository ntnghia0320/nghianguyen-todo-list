package com.ntnghia.task.repository;

import com.ntnghia.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByTitleContainsOrDescriptionContains(String keywordInTitle, String keywordInDescription);

    Task findByTitleAndDescription(String title, String description);
}
