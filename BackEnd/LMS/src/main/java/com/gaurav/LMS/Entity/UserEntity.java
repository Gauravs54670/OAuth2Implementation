package com.gaurav.LMS.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor @NoArgsConstructor
@Data @Builder
@Entity @Table(name = "user_entity")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true)
    private String contact;
    private String password;
    private String firstName;
    private String lastName;
    private String bio;
    private LocalDateTime accountCreatedAt;
    private LocalDateTime accountUpdateAt;
    private String resetToken;
    private Instant tokenExpirationTime;
}
