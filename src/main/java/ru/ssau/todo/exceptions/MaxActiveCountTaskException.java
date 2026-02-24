package ru.ssau.todo.exceptions;

public class MaxActiveCountTaskException extends RuntimeException {
    public MaxActiveCountTaskException() {
        super("Достигнут максимум активных задач");
    }
}
