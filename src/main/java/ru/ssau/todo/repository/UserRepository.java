package ru.ssau.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.todo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
