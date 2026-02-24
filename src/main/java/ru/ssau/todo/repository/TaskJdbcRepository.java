package ru.ssau.todo.repository;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exceptions.TaskNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
@AllArgsConstructor
public class TaskJdbcRepository implements TaskRepository{

    private final JdbcTemplate jdbcTemplate;

    private Task createTaskFromDB(ResultSet resultSet) throws SQLException {
        Task t = new Task();
        t.setId(resultSet.getLong("id"));
        t.setTitle(resultSet.getString("title"));
        t.setStatus(TaskStatus.valueOf(resultSet.getString("status")));
        t.setCreatedBy(resultSet.getLong("created_by"));
        t.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        return t;
    }

    @Override
    public Task create(Task task) {
        if (task == null) throw new IllegalArgumentException();
        task.setCreatedAt(LocalDateTime.now().withNano(0));
        task.setId(
        jdbcTemplate.queryForObject("insert into task (title, status, created_by, created_at) values (?, ?, ?, ?) returning task.id",
                Long.class, task.getTitle(), task.getStatus().toString(), task.getCreatedBy(), task.getCreatedAt()));
        return task;
    }

    @Override
    public Optional<Task> findById(long id) {
        try {
            Task task = jdbcTemplate.queryForObject(
                    "SELECT * FROM task WHERE id = ?",
                    (resultSet, rowNum) -> createTaskFromDB(resultSet),
                    id
            );
            return Optional.ofNullable(task);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return this.jdbcTemplate.query(
                "SELECT * FROM task where (created_by = ?) And (created_at <= ?) and (created_at >= ?)",
                (resultSet, rowNum) -> createTaskFromDB(resultSet), userId, to, from);
    }

    @Override
    public void update(Task task) throws TaskNotFoundException {
        int affectedRow = this.jdbcTemplate.update("update task set title = ?, status = ? where task.id = ?", task.getTitle(), task.getStatus().toString(), task.getId());
        if (affectedRow == 0) throw new TaskNotFoundException();
    }

    @Override
    public void deleteById(long id) {
        this.jdbcTemplate.update("delete from task where task.id = ?", id);
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        return this.jdbcTemplate.queryForObject("select count(task.id) from task where (created_by = ? and (status in ('OPEN', 'IN_PROGRESS')))",
                Long.class, userId);
    }
}
