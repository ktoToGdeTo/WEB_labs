package ru.ssau.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.exceptions.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;
import ru.ssau.todo.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ContentNegotiatingViewResolver contentNegotiatingViewResolver;

    @Autowired
    private RequestToViewNameTranslator requestToViewNameTranslator;

    public TaskController(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    @GetMapping()
    public ResponseEntity<List<Task>> getTasks(@RequestParam(value = "from", required = false)LocalDateTime from, @RequestParam(value = "to", required = false) LocalDateTime to,
                                               @RequestParam(value = "userId") Long userId){
        if(from == null) from = LocalDateTime.MIN;
        if(to == null) to = LocalDateTime.MAX;
        return ResponseEntity.ok(taskRepository.findAll(from, to, userId));
    }

    @PostMapping()
    public ResponseEntity<Task> createTask(@RequestBody Task task){
        Task createdtask = taskService.createTask(task);
        if (createdtask != null) return ResponseEntity.status(HttpStatus.CREATED).header("Location", "/tasks/"+createdtask.getId()).body(createdtask);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Task>> getTask(@PathVariable(name = "id") Long id){
        Optional<Task> foundedTask = taskRepository.findById(id);
        if (foundedTask.isPresent()) return ResponseEntity.status(HttpStatus.OK).body(foundedTask);
        else return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable(name = "id") Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> countActiveTasks(@RequestParam(value = "userId") Long userId){
        return ResponseEntity.ok(taskRepository.countActiveTasksByUserId(userId));
    }
}
