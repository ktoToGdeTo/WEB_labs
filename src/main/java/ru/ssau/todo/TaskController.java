package ru.ssau.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.exceptions.MaxActiveCountTaskException;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping()
    public ResponseEntity<List<Task>> getTasks(@RequestParam(value = "from", required = false) LocalDateTime from, @RequestParam(value = "to", required = false) LocalDateTime to,
                                               @RequestParam(value = "userId") Long userId) {
        if (from == null) from = LocalDateTime.MIN;
        if (to == null) to = LocalDateTime.MAX;
        if (from.isAfter(to)) {
            LocalDateTime tempDate = to;
            to = from;
            from = tempDate;
        }
        return ResponseEntity.ok(taskService.findAll(from, to, userId));
    }

    @PostMapping()
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdtask = taskService.createTask(task);
        if (createdtask != null)
            return ResponseEntity.status(HttpStatus.CREATED).header("Location", "/tasks/" + createdtask.getId()).body(createdtask);
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Task>> getTask(@PathVariable(name = "id") Long id) {
        Optional<Task> foundedTask = taskService.findById(id);
        if (foundedTask.isPresent()) return ResponseEntity.status(HttpStatus.OK).body(foundedTask);
        else return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> refreshTask(@PathVariable(name = "id") Long id, @RequestBody Task task) throws Exception {
        task.setId(id);
        try {
            taskService.updateTask(task);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (MaxActiveCountTaskException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The user has the maximum number of tasks.");
        }
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable(name = "id") Long id) {
        taskService.deleteTask(id);
        if (taskService.findById(id).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body("Task not found.");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> countActiveTasks(@RequestParam(value = "userId") Long userId) {
        return ResponseEntity.ok(taskService.countActiveTasksByUserId(userId));
    }
}
