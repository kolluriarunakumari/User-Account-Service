package com.microservice;

import com.microservice.model.User;
import com.microservice.repository.UserRepository;
import com.microservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .userId("USR-10001")
                .fullName("Alice Johnson")
                .email("alice222@example.com")
                .status("ACTIVE")
                .build();
    }

    @Test
    void getAllUsers_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        List<User> users = userService.getAllUsers();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUserId()).isEqualTo("USR-10001");
    }

    @Test
    void getUserByUserId_found() {
        when(userRepository.findByUserId("USR-10001")).thenReturn(Optional.of(sampleUser));
        Optional<User> result = userService.getUserByUserId("USR-10001");
        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("Alice Johnson");
    }

    @Test
    void getUserByUserId_notFound() {
        when(userRepository.findByUserId("INVALID")).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserByUserId("INVALID");
        assertThat(result).isEmpty();
    }

    @Test
    void createUser_success() {
        when(userRepository.existsByUserId(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(sampleUser);
        User created = userService.createUser(sampleUser);
        assertThat(created.getUserId()).isEqualTo("USR-10001");
    }

    @Test
    void createUser_duplicateUserId_throwsException() {
        when(userRepository.existsByUserId("USR-10001")).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(sampleUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID already exists");
    }
}
