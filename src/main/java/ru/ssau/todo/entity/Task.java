package ru.ssau.todo.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Task {
    private long id;
    private String title;
    private TaskStatus status;
    private long createdBy;
    private LocalDateTime createdAt;
}
