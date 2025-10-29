package com.usa.data_manager.controllers;

import com.usa.data_manager.model.User;
import com.usa.data_manager.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            model.addAttribute("contactCount", user.getContacts().size());
            logger.info("Dashboard accessed by user: {}", email);
        } else {
            logger.warn("User not found for email: {}", email);
            return "redirect:/loginPage";
        }

        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            logger.info("Profile accessed by user: {}", email);
        } else {
            logger.warn("User not found for email: {}", email);
            return "redirect:/loginPage";
        }

        return "user/profile";
    }

    @GetMapping("/settings")
    public String userSettings(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            logger.info("Settings accessed by user: {}", email);
        } else {
            logger.warn("User not found for email: {}", email);
            return "redirect:/loginPage";
        }

        return "user/settings";
    }

    @PostMapping("/settings/update")
    public String updateUserSettings(
            @RequestParam("name") String name,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "about", required = false) String about,
            @RequestParam(value = "profilePic", required = false) String profilePic,
            RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                user.setName(name);
                user.setPhoneNumber(phoneNumber);
                user.setAbout(about);
                user.setProfilePic(profilePic);
                
                userService.updateUser(user);
                logger.info("User settings updated successfully for: {}", email);
                
                redirectAttributes.addFlashAttribute("successMessage", "Settings updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            }
        } catch (Exception e) {
            logger.error("Error updating user settings: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update settings. Please try again.");
        }

        return "redirect:/user/settings";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match!");
                return "redirect:/user/settings";
            }

            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 6 characters long!");
                return "redirect:/user/settings";
            }

            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect!");
                    return "redirect:/user/settings";
                }
                
                user.setPassword(passwordEncoder.encode(newPassword));
                userService.updateUser(user);
                logger.info("Password changed successfully for: {}", email);
                
                redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            }
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password. Please try again.");
        }

        return "redirect:/user/settings";
    }

    @PostMapping("/settings/delete-account")
    public String deleteAccount(
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                if (!passwordEncoder.matches(confirmPassword, user.getPassword())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Password is incorrect!");
                    return "redirect:/user/settings";
                }
                
                userService.deleteUser(user.getUserId());
                logger.info("Account deleted for user: {}", email);
                
                return "redirect:/logout";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            }
        } catch (Exception e) {
            logger.error("Error deleting account: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete account. Please try again.");
        }

        return "redirect:/user/settings";
    }
}
