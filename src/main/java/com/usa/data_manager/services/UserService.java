package com.usa.data_manager.services;

import com.usa.data_manager.jpa.UserRepository;
import com.usa.data_manager.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Save user
    public User saveUser(User user) {
        // Generate unique user ID if not present
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId(UUID.randomUUID().toString());
        }
        
        // Encode password when provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        logger.info("Saving user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    // Get user by ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Update user
    public User updateUser(User user) {
        logger.info("Updating user with ID: {}", user.getUserId());
        return userRepository.save(user);
    }

    // Delete user
    public void deleteUser(String userId) {
        logger.info("Deleting user with ID: {}", userId);
        userRepository.deleteById(userId);
    }

    // Check if user exists by email
    public boolean isUserExistByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Register new user
    public User registerUser(User user) throws Exception {
        // Check if user already exists
        if (isUserExistByEmail(user.getEmail())) {
            throw new Exception("User with email " + user.getEmail() + " already exists");
        }

        // Set default values for new registration
        user.setEnabled(true); // Enable user by default

        return saveUser(user);
    }

    // Authenticate user (for login)
    public boolean authenticateUser(String email, String rawPassword) {
        Optional<User> userOptional = getUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.isEnabled()) {
                return passwordEncoder.matches(rawPassword, user.getPassword());
            }
        }
        return false;
    }

    // Enable/Disable user
    public void updateUserStatus(String userId, boolean enabled) {
        Optional<User> userOptional = getUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(enabled);
            updateUser(user);
            logger.info("User {} status updated to: {}", userId, enabled);
        }
    }
}
