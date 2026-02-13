package ru.ssau.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.repository.TaskJdbcRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Profile("jdbc")
@AllArgsConstructor
public class TaskJdbcService implements TaskService{

    private final JdbcTemplate jdbcTemplate;

    private final TaskJdbcRepository taskJdbcRepository;

    @Override
    public void deleteTask(long id) {
        try {
            LocalDateTime createdTaskTime = this.jdbcTemplate.queryForObject("select created_at from task where task.id = ?", LocalDateTime.class, id);
            if (ChronoUnit.MINUTES.between(createdTaskTime, LocalDateTime.now().withNano(0))>5) taskJdbcRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException ignored){
        }
    }

    @Override
    public Task createTask(Task task) {
        if (this.taskJdbcRepository.countActiveTasksByUserId(task.getCreatedBy())<10) return this.taskJdbcRepository.create(task);
        return null;
    }
}
