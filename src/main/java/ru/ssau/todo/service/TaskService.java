package ru.ssau.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exceptions.MaxActiveCountTaskException;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {

    private final int DELETE_TIME = 5;
    private final int MAX_ACTIVE_TASKS = 10;

    private final TaskRepository taskRepository;

    public void deleteTask(long id) {
        LocalDateTime createdTaskTime = taskRepository.findById(id).get().getCreatedAt();
        if (ChronoUnit.MINUTES.between(createdTaskTime, LocalDateTime.now().withNano(0)) > DELETE_TIME)
            taskRepository.deleteById(id);
    }

    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return taskRepository.findAll(from, to, userId);
    }

    public Optional<Task> findById(long id) {
        return taskRepository.findById(id);
    }

    public void updateTask(Task task) throws TaskNotFoundException, MaxActiveCountTaskException {
        if (taskRepository.countActiveTasksByUserId(taskRepository.findById(task.getId()).get().getCreatedBy()) < MAX_ACTIVE_TASKS) {
            taskRepository.update(task);
            return;
        } else if (!task.getStatus().equals(TaskStatus.OPEN) && !task.getStatus().equals(TaskStatus.IN_PROGRESS))
        {
            taskRepository.update(task);
            return;
        }
        throw new MaxActiveCountTaskException();
    }

    public Task createTask(Task task) {
        if ((taskRepository.countActiveTasksByUserId(task.getCreatedBy()) < MAX_ACTIVE_TASKS)
        || (task.getStatus().equals(TaskStatus.CLOSED) || task.getStatus().equals(TaskStatus.DONE))) return this.taskRepository.create(task);
        return null;
    }

    public long countActiveTasksByUserId(long id) {
        return taskRepository.countActiveTasksByUserId(id);
    }
}
