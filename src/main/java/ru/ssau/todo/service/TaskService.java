package ru.ssau.todo.service;

import ru.ssau.todo.entity.Task;

public interface TaskService {
    void deleteTask(long id);

    Task createTask(Task task);
}
