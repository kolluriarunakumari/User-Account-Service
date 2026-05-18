package com.microservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @Column(name = "account_type", nullable = false)
    @NotBlank(message = "Account type is required")
    private String accountType;  // SAVINGS, CHECKING, PREMIUM

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "user_id", nullable = false)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
