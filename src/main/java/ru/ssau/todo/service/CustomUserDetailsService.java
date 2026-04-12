package ru.ssau.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Role;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.entity.dto.UserDto;
import ru.ssau.todo.exceptions.UserAlreadyRegister;
import ru.ssau.todo.repository.RoleRepository;
import ru.ssau.todo.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException(username);
        return user;
    }

    public void registerUser(UserDto userDto) {
        if(userRepository.findByUsername(userDto.getUsername()) != null) throw new UserAlreadyRegister();
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setTasks(new ArrayList<>());

        List<Role> roles = new ArrayList<>();
        if (userDto.getUsername().equalsIgnoreCase("admin")) roles.add(roleRepository.findById(1L).orElseThrow());
        else roles.add(roleRepository.findById(2L).orElseThrow());
        user.setRoles(roles);

        userRepository.save(user);
    }
}
