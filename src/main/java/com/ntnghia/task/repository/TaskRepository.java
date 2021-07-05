package com.ntnghia.task.repository;

import com.ntnghia.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT t FROM Task t WHERE title LIKE %:keyword% OR description LIKE %:keyword%")
    List<Task> findByKeyword(String keyword);

    @Query("SELECT t FROM Task t WHERE title = :title AND description = :description")
    Task findByTitleAndDescription(String title, String description);
}
