package ru.ssau.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.entity.dto.TaskDto;
import ru.ssau.todo.entity.dto.TaskStatusDto;
import ru.ssau.todo.exceptions.MaxActiveCountTaskException;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;
import ru.ssau.todo.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskService {

    private final int DELETE_TIME = 0;
    private final int MAX_ACTIVE_TASKS = 3;

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private TaskDto taskToDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setCreatedBy(task.getUser().getId());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setStatus(task.getStatus());
        return taskDto;
    }

    private boolean isActive(TaskDto task) {
        if (task.getStatus().equals(TaskStatus.IN_PROGRESS) || task.getStatus().equals(TaskStatus.OPEN)) return true;
        return false;
    }

    private boolean isMaxCount(long id) {
        return taskRepository.countActiveTasksByUserId(id) >= MAX_ACTIVE_TASKS;
    }

    private LocalDateTime getNow() {
        return LocalDateTime.now().withNano(0);
    }

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
        Optional<Task> foundTask = taskRepository.findById(taskDto.getId());
        if(foundTask.isEmpty()) throw new TaskNotFoundException();
        Task task = foundTask.get();
        if(taskDto.getStatus().equals(task.getStatus())) {
            taskRepository.save(task);
            return;
        }
        if(!isActive(taskToDto(task)) && isActive(taskDto)){
            if(isMaxCount(task.getUser().getId())) throw new MaxActiveCountTaskException();
        }
        task.setTitle(taskDto.getTitle());
        task.setStatus(taskDto.getStatus());
        taskRepository.save(task);
    }

    public TaskDto createTask(TaskDto taskDto, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        if (user == null) return null;
        if (isActive(taskDto) && isMaxCount(user.getId())) return null;
        Task task = new Task();
        task.setStatus(taskDto.getStatus());
        task.setCreatedAt(getNow());
        task.setUser(user);
        task.setTitle(taskDto.getTitle());
        taskRepository.save(task);
        return taskToDto(task);
    }

    public long countActiveTasksByUserId(long id) {
        return taskRepository.countActiveTasksByUserId(id);
    }

    public Map<String, Long> countStatusTasks() {
        return taskRepository.countTasksStatus().stream().collect(Collectors.toMap(TaskStatusDto::getStatus, TaskStatusDto::getCount));
    }
}
