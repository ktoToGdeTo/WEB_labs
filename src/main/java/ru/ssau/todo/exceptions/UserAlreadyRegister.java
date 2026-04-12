package ru.ssau.todo.exceptions;

public class UserAlreadyRegister extends RuntimeException {
    public UserAlreadyRegister() {
        super("Пользователь уже был зарегистрирован с таким именем.");
    }
}
