package com.closedwallet.Entity;

import com.closedwallet.enums.Role;
import com.closedwallet.enums.KycStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "user")
    private Wallet wallet;

    private String name;
    private String phoneNumber;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String phoneNumber, String name, String password, String email) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public User() {
    }
}
