package ru.ssau.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.dto.TaskStatusDto;
import ru.ssau.todo.exceptions.TaskNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления жизненным циклом сущностей {@link Task}.
 * Обеспечивает абстракцию над механизмом хранения данных.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Выполняет поиск задачи по её уникальному идентификатору.
     *
     * @param id уникальный идентификатор задачи.
     * @return {@link Optional}, содержащий найденную задачу,
     * или пустой Optional, если задача с таким ID не найдена.
     */
    Optional<Task> findById(long id);

    /**
     * Возвращает список всех задач конкретного пользователя, созданных в указанном временном диапазоне.
     *
     * @param from   начальная граница даты создания (включительно).
     * @param to     конечная граница даты создания (включительно).
     * @param userId уникальный идентификатор пользователя-владельца.
     * @return список задач, соответствующих критериям поиска. Если ничего не найдено, возвращается пустой список.
     */
    @Query(nativeQuery = true,
    value = "select * from task where created_at between :from and :to and created_by = :userId ")
    List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId);

    /**
     * Подсчитывает количество "активных" задач для конкретного пользователя.
     * Активной считается задача, находящаяся в статусе OPEN или IN_PROGRESS.
     *
     * @param userId идентификатор пользователя.
     * @return количество активных задач.
     */
    @Query(value = "select count(t) from Task t where t.user.id = :userId and status in ('OPEN', 'IN_PROGRESS')")
    long countActiveTasksByUserId(long userId);

    @Query(nativeQuery = true, value = "select s.status, count(t.status) from (values('OPEN'::text), ('CLOSED'::text), ('IN_PROGRESS'::text), ('DONE'::text)) as s(status) left join task t on t.status = s.status group by s.status")
    List<TaskStatusDto> countTasksStatus();
}
