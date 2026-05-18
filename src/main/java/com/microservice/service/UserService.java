package com.microservice.service;

import com.microservice.model.User;
import com.microservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public User createUser(User user) {
        if (userRepository.existsByUserId(user.getUserId())) {
            throw new IllegalArgumentException("User ID already exists: " + user.getUserId());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public Optional<User> updateUser(String userId, User updatedUser) {
        return userRepository.findByUserId(userId).map(existing -> {
            existing.setFullName(updatedUser.getFullName());
            existing.setEmail(updatedUser.getEmail());
            existing.setPhoneNumber(updatedUser.getPhoneNumber());
            existing.setStatus(updatedUser.getStatus());
            return userRepository.save(existing);
        });
    }

    public boolean deleteUser(String userId) {
        return userRepository.findByUserId(userId).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}
