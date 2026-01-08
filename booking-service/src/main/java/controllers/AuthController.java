package controllers;

import models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody Map<String, Object> req) {
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        return authService.createUser(username, password);
    }

    @PostMapping("/auth")
    public ResponseEntity<String> authUser(@RequestBody Map<String, Object> req) {
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        String token = authService.login(username, password);
        return ResponseEntity.ok(token);
    }
}
