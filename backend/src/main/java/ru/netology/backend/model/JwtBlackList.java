package ru.netology.backend.model;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "blacklist")
public class JwtBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
}
