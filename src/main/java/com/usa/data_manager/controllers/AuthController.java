package com.usa.data_manager.controllers;

import com.usa.data_manager.dto.UserRegistrationDto;
import com.usa.data_manager.model.Providers;
import com.usa.data_manager.model.User;
import com.usa.data_manager.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "auth/signup";
    }

    @GetMapping("/register")
    public String redirectToSignup() {
        return "redirect:/signup";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto registrationDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        logger.info("Processing registration for email: {}", registrationDto.getEmail());

        if (bindingResult.hasErrors()) {
            logger.error("Validation errors during registration:");
            bindingResult.getAllErrors().forEach(error -> 
                logger.error("Validation error: {}", error.getDefaultMessage())
            );
            model.addAttribute("userRegistrationDto", registrationDto);
            return "auth/signup";
        }

        if (!registrationDto.isPasswordMatching()) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("userRegistrationDto", registrationDto);
            return "auth/signup";
        }

        try {
            if (userService.isUserExistByEmail(registrationDto.getEmail())) {
                model.addAttribute("error", "User with this email already exists");
                model.addAttribute("userRegistrationDto", registrationDto);
                return "auth/signup";
            }

            User user = User.builder()
                .name(registrationDto.getName())
                .email(registrationDto.getEmail())
                .password(registrationDto.getPassword())
                .phoneNumber(registrationDto.getPhoneNumber())
                .about(registrationDto.getAbout())
                .enabled(true)
                .emailVerified(false)
                .phoneVerified(false)
                .provider(Providers.SELF)
                .emailNotifications(true)
                .twoFactorEnabled(false)
                .build();

            User savedUser = userService.registerUser(user);
            logger.info("User registered successfully with ID: {}", savedUser.getUserId());

            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/loginPage";

        } catch (Exception e) {
            logger.error("Error during user registration: ", e);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("userRegistrationDto", registrationDto);
            return "auth/signup";
        }
    }

    @GetMapping("/loginPage")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               Model model,
                               HttpSession session) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                model.addAttribute("contactCount", user.getContacts().size());
                logger.info("Dashboard accessed by user: {}", email);
                return "dashboard";
            } else {
                logger.warn("User not found for email: {}", email);
                return "redirect:/loginPage";
            }
        } catch (Exception e) {
            logger.error("Error loading dashboard: ", e);
            return "redirect:/loginPage";
        }
    }
}
