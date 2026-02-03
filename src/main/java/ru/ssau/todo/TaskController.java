package ru.ssau.todo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class TaskController {

    private final TaskRepository taskRepository;


    public TaskController(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getTasks(@RequestParam(value = "from", required = false)LocalDateTime from, @RequestParam(value = "to", required = false) LocalDateTime to,
                                               @RequestParam(value = "userId") Long userId){
        if(from == null) from = LocalDateTime.MIN;
        if(to == null) to = LocalDateTime.MAX;
        return ResponseEntity.ok(taskRepository.findAll(from, to, userId));
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task){
        Task createdtask = taskRepository.create(task);
        return ResponseEntity.status(HttpStatus.CREATED).header("Location", "/tasks/"+createdtask.getId()).body(createdtask);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Optional<Task>> getTask(@PathVariable(name = "id") Long id){
        Optional<Task> foundedTask = taskRepository.findById(id);
        if (foundedTask.isPresent()) return ResponseEntity.status(HttpStatus.OK).body(foundedTask);
        else return ResponseEntity.notFound().build();
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<Void> refreshTask(@PathVariable(name = "id") Long id, @RequestBody Task task) throws Exception {
        task.setId(id);
        try {
            taskRepository.update(task);
        }
        catch (TaskNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable(name = "id") Long id){
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("tasks/active/count")
    public ResponseEntity<Long> countActiveTasks(@RequestParam(value = "userId") Long userId){
        return ResponseEntity.ok(taskRepository.countActiveTasksByUserId(userId));
    }
}
