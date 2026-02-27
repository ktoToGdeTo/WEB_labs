package ru.ssau.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.entity.dto.TaskDto;
import ru.ssau.todo.exceptions.MaxActiveCountTaskException;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;
import ru.ssau.todo.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {

    private final int DELETE_TIME = 5;
    private final int MAX_ACTIVE_TASKS = 3;

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private TaskDto taskToDto(Task task){
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setCreatedBy(task.getUser().getId());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setStatus(TaskStatus.valueOf(task.getStatus()));
        return taskDto;
    }

    private LocalDateTime getNow() { return LocalDateTime.now().withNano(0); }

    public void deleteTask(long id) {
        LocalDateTime createdTaskTime = taskRepository.findById(id).get().getCreatedAt();
        if (ChronoUnit.MINUTES.between(createdTaskTime, getNow()) > DELETE_TIME)
            taskRepository.deleteById(id);
    }

    public List<TaskDto> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        List<Task> tasks = taskRepository.findAll(from, to, userId);
        return tasks.stream().map(this::taskToDto).toList();
    }

    public Optional<TaskDto> findById(long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(this::taskToDto);
    }

    public void updateTask(TaskDto taskDto) throws TaskNotFoundException, MaxActiveCountTaskException {
        Optional<Task> task = taskRepository.findById(taskDto.getId());
        if(task.isEmpty()) throw new TaskNotFoundException();
        Task t = task.get();
        t.setTitle(taskDto.getTitle());
        t.setStatus(taskDto.getStatus().toString());
        System.err.println(taskRepository.countActiveTasksByUserId(t.getUser().getId()));
        if (taskRepository.countActiveTasksByUserId(t.getUser().getId()) < MAX_ACTIVE_TASKS) {
            taskRepository.save(t);
        } else if (!taskDto.getStatus().equals(TaskStatus.OPEN) && !taskDto.getStatus().equals(TaskStatus.IN_PROGRESS))
        {
            taskRepository.save(t);
        }
        else throw new MaxActiveCountTaskException();
    }

    public TaskDto createTask(TaskDto taskDto) {
        Optional<User> user = userRepository.findById(taskDto.getCreatedBy());
        if(user.isEmpty()) return null;
        if ((taskRepository.countActiveTasksByUserId(user.get().getId()) < MAX_ACTIVE_TASKS)
        || (taskDto.getStatus().equals(TaskStatus.CLOSED) || taskDto.getStatus().equals(TaskStatus.DONE))) {
            Task task = new Task();
            task.setStatus(taskDto.getStatus().toString());
            task.setCreatedAt(getNow());
            task.setUser(user.get());
            task.setTitle(taskDto.getTitle());
            this.taskRepository.save(task);
            return taskToDto(task);
        }
        return null;
    }

    public long countActiveTasksByUserId(long id) {
        return taskRepository.countActiveTasksByUserId(id);
    }
}
