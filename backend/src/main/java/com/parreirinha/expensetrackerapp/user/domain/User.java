package com.parreirinha.expensetrackerapp.user.domain;

import java.util.UUID;

import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String keycloakId;

    private String username;

    private String email;

}
