package ru.ssau.todo.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.resilience.annotation.RetryAnnotationBeanPostProcessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.entity.dto.UserDto;
import ru.ssau.todo.repository.UserRepository;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getAuthorizatedUser(Authentication auth) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("username", auth.getName());
        user.put("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(a -> a.contains("ROLE_")).toList());
        user.put("userId", ((User) auth.getPrincipal()).getId());

        return ResponseEntity.ok(user);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(
//            @RequestBody UserDto userDto,
//            HttpServletRequest request,
//            HttpServletResponse response) {
//        try {
//            Authentication auth = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            userDto.getUsername(),
//                            userDto.getPassword()));
//            SecurityContext context = SecurityContextHolder.createEmptyContext();
//            context.setAuthentication(auth);
//            SecurityContextHolder.setContext(context);
//            securityContextRepository.saveContext(context, request, response);
//            return ResponseEntity.ok(Map.of("message", "Login successful."));
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Incorrect login or password!");
//        }
//    }

    @GetMapping("/success")
    public String success() {
        return "Login successful";
    }

    @GetMapping("/error")
    public String error() {
        return "Login failed";
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkSession(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
