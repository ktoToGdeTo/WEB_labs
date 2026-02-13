package ru.ssau.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.repository.TaskInMemoryRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Service
@Profile("inMemory")
@AllArgsConstructor
public class TaskInMemoryService implements TaskService{

    @Autowired
    private TaskInMemoryRepository taskInMemoryRepository;

    @Override
    public void deleteTask(long id) {
            if (ChronoUnit.MINUTES.between(taskInMemoryRepository.findById(id).get().getCreatedAt(), LocalDateTime.now())>5) taskInMemoryRepository.deleteById(id);
    }

    @Override
    public Task createTask(Task task) {
        if (taskInMemoryRepository.countActiveTasksByUserId(task.getCreatedBy())<10) return this.taskInMemoryRepository.create(task);
        return null;
    }
}
