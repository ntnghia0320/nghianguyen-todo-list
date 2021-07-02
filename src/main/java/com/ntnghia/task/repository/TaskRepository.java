package com.ntnghia.task.repository;

import com.ntnghia.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT t FROM Task t WHERE title LIKE %:keyWord% OR description LIKE %:keyWord%")
    Task findByKeyWord(String keyWord);
}
