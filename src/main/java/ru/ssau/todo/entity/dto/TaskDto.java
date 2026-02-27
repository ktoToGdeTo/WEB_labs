package ru.ssau.todo.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ssau.todo.entity.TaskStatus;


import java.time.LocalDateTime;

@Getter
@Setter
public class TaskDto {
    private long id;
    private String title;
    private TaskStatus status;
    private long createdBy;
    private LocalDateTime createdAt;

}
