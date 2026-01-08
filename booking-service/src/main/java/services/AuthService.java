package services;

import errors.InvalidCredentialsException;
import errors.UserNotFoundException;
import io.jsonwebtoken.Jwts;
import models.User;
import models.UserRole;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import repositories.UserRepository;
import security.Security;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final SecretKey key;

    public AuthService(UserRepository userRepository, String secret) {
        this.userRepository = userRepository;
        this.key = Security.getSecretKey(secret);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User createUser(String username, String password) {
        User user = new User();
        user.username = username;
        user.password = BCrypt.hashpw(password, BCrypt.gensalt());
        user.role = UserRole.USER;
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (!BCrypt.checkpw(password, user.password)) {
            throw new InvalidCredentialsException();
        }

        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(user.id.toString())
            .addClaims(Map.of(
                    "role", user.role,
                    "username", user.username
            ))
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(3600)))
            .signWith(key)
            .compact();
    }
}
