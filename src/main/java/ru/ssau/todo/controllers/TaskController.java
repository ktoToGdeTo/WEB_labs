package ru.ssau.todo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.entity.dto.TaskDto;
import ru.ssau.todo.exceptions.MaxActiveCountTaskException;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping()
    public ResponseEntity<List<TaskDto>> getTasks(@RequestParam(value = "from", required = false) LocalDateTime from, @RequestParam(value = "to", required = false) LocalDateTime to,
                                                  @RequestParam(value = "userId") Long userId) {
        if (from == null) from = LocalDateTime.of(1970, 1, 1, 0, 0);
        if (to == null) to = LocalDateTime.of(3001, 1, 1, 0, 0);
        if (from.isAfter(to)) {
            LocalDateTime tempDate = to;
            to = from;
            from = tempDate;
        }
        return ResponseEntity.ok(taskService.findAll(from, to, userId));
    }

    @PostMapping()
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto, Authentication auth) {
        TaskDto createdtask = taskService.createTask(taskDto, auth);
        if (createdtask != null)
            return ResponseEntity.status(HttpStatus.CREATED).header("Location", "/tasks/" + createdtask.getId()).body(createdtask);
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<TaskDto>> getTask(@PathVariable(name = "id") Long id) {
        Optional<TaskDto> foundedTask = taskService.findById(id);
        if (foundedTask.isPresent()) return ResponseEntity.status(HttpStatus.OK).body(foundedTask);
        else return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> refreshTask(@PathVariable(name = "id") Long id, @RequestBody TaskDto taskDto) throws Exception {
        taskDto.setId(id);
        try {
            taskService.updateTask(taskDto);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (MaxActiveCountTaskException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable(name = "id") Long id) {
        taskService.deleteTask(id);
        if (taskService.findById(id).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> countActiveTasks(@RequestParam(value = "userId") Long userId) {
        return ResponseEntity.ok(taskService.countActiveTasksByUserId(userId));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countTasks() {
        return ResponseEntity.ok().body(taskService.countStatusTasks());
    }
}
