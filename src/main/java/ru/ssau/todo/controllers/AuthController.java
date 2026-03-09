package ru.ssau.todo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getAuthorizatedUser(Authentication auth) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("username", auth.getName());
        user.put("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(a -> a.contains("ROLE_")).toList());

        return ResponseEntity.ok(user);
    }
}
