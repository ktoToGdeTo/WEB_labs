package ru.ssau.todo.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {
        super("Задача не была найдена.");
    }
}
