package com.anki.anki_api.controller;

import com.anki.anki_api.dto.JwtResponse;
import com.anki.anki_api.dto.LoginRequest;
import com.anki.anki_api.dto.SignupRequest;
import com.anki.anki_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    com.anki.anki_api.security.JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        // Wait, authentication needs to be passed to jwtUtils but userService already does the authentication!
        // We need to modify UserService to return Authentication or do it here.
        // Actually, let's just authenticate here instead of UserService.
        JwtResponse response = userService.authenticateUser(loginRequest);
        
        // userService.authenticateUser sets SecurityContextHolder
        authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        org.springframework.http.ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(authentication);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        org.springframework.http.ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You've been signed out!");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            userService.registerUser(signUpRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Autowired
    com.anki.anki_api.repository.UserRepository userRepository;
    
    @Autowired
    com.anki.anki_api.repository.CardAssignmentRepository cardAssignmentRepository;

    @Autowired
    com.anki.anki_api.repository.LearningHistoryRepository learningHistoryRepository;

    @GetMapping("/students")
    public org.springframework.data.domain.Page<java.util.Map<String, Object>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Page<com.anki.anki_api.entity.User> studentPage = userRepository.findByRole(
                com.anki.anki_api.entity.Role.ROLE_STUDENT, 
                org.springframework.data.domain.PageRequest.of(page, size));
                
        return studentPage.map(user -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("fullName", user.getFullName());
            map.put("email", user.getEmail());
            return map;
        });
    }

    @DeleteMapping("/students/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok().body("Student deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting student: " + e.getMessage());
        }
    }
}
