package ru.ssau.todo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.entity.dto.UserDto;
import ru.ssau.todo.exceptions.UserAlreadyRegister;
import ru.ssau.todo.service.CustomUserDetailsService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try{
            userDetailsService.registerUser(userDto);
        }
        catch (UserAlreadyRegister e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь уже есть с таким именем. Используйте другое.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
