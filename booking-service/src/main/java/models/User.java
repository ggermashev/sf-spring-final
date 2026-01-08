package models;

import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String username;
    public String password;

    @Enumerated(EnumType.STRING)
    public UserRole role;

    public Long balance;
}
