package ru.ssau.todo.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exceptions.TaskNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Profile("inMemory")
public class TaskInMemoryRepository implements TaskRepository{

    private final Map<Long, Task> tasks;

    public TaskInMemoryRepository(){
        tasks = new HashMap<>();
    }

    @Override
    public Task create(Task task) {
        if (task == null) throw new IllegalArgumentException();
        task.setCreatedAt(LocalDateTime.now().withNano(0));
        if (tasks.isEmpty()) {
            task.setId(1);
        }
        else
        {
            task.setId((long) tasks.keySet().toArray()[tasks.size()-1]+1);
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        List<Task> result = new ArrayList<>();
        for(Task task : tasks.values()){
            if((!task.getCreatedAt().isBefore(from))
                    &&(!task.getCreatedAt().isAfter(to))
                    &&(task.getCreatedBy()==userId))
                result.add(task);
        }
        return result;
    }

    @Override
    public void update(Task task) throws TaskNotFoundException{
        for(Task t : tasks.values()){
            if(task.getId() == t.getId()){
                t.setTitle(task.getTitle());
                t.setStatus(task.getStatus());
                return;
            }
        }
        throw new TaskNotFoundException();
    }

    @Override
    public void deleteById(long id) {
        tasks.remove(id);
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        long result = 0;
        for(Task task : tasks.values()){
            if ((task.getCreatedBy() == userId)&&
                    (task.getStatus()== TaskStatus.OPEN)
                    ||task.getStatus()==TaskStatus.IN_PROGRESS)
                result++;
        }
        return result;
    }
}
