package com.anki.anki_api.controller;

import com.anki.anki_api.dto.UserDTO;
import com.anki.anki_api.entity.Role;
import com.anki.anki_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/students")
    @PreAuthorize("hasRole('TEACHER')")
    public List<UserDTO> getStudents() {
        return userRepository.findByRole(Role.ROLE_STUDENT).stream()
                .map(u -> UserDTO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .fullName(u.getFullName())
                        .build())
                .toList();
    }
}

