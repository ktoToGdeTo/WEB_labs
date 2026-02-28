package ru.ssau.todo.service;

import lombok.AllArgsConstructor;
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

    private boolean isActive(Task task){
        if (task.getStatus().equals(TaskStatus.IN_PROGRESS) || task.getStatus().equals(TaskStatus.OPEN)) return true;
        return false;
    }

    private boolean isMaxCount(Task task){
        return taskRepository.countActiveTasksByUserId(task.getCreatedBy()) >= MAX_ACTIVE_TASKS;
    }


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
        Optional<Task> foundTask = taskRepository.findById(task.getId());
        if(foundTask.isEmpty()) throw new TaskNotFoundException();
        if(foundTask.get().getStatus().equals(task.getStatus())) taskRepository.update(task);
        if(!isActive(foundTask.get()) && isActive(task)){
            if(isMaxCount(foundTask.get())) throw new MaxActiveCountTaskException();
        }
        taskRepository.update(task);
    }

    public Task createTask(Task task) {
        if (isActive(task) && isMaxCount(task)) return null;
        return this.taskRepository.create(task);
    }

    public long countActiveTasksByUserId(long id) {
        return taskRepository.countActiveTasksByUserId(id);
    }
}
