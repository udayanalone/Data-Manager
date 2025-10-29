package com.usa.data_manager.controllers;

import com.usa.data_manager.model.Contact;
import com.usa.data_manager.model.User;
import com.usa.data_manager.services.ContactService;
import com.usa.data_manager.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    // View all contacts
    @GetMapping
    public String viewContacts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "filter", required = false) String filter,
            Model model) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Contact> contacts;

            if (search != null && !search.isEmpty()) {
                contacts = contactService.searchContacts(user, search);
                model.addAttribute("searchQuery", search);
            } else if ("favorites".equals(filter)) {
                contacts = contactService.getFavoriteContacts(user);
                model.addAttribute("currentFilter", filter);
            } else {
                contacts = contactService.getContactsByUser(user);
            }

            model.addAttribute("contacts", contacts);
            model.addAttribute("contactCount", contactService.countContactsByUser(user));
            model.addAttribute("user", user);
            
            logger.info("Contacts page accessed by user: {}", email);
        } else {
            logger.warn("User not found for email: {}", email);
            return "redirect:/loginPage";
        }

        return "user/contacts";
    }

    // Show add contact form
    @GetMapping("/add")
    public String showAddContactForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("contact", new Contact());
            model.addAttribute("isEdit", false);
            return "user/contact-form";
        }
        
        return "redirect:/loginPage";
    }

    // Process add contact
    @PostMapping("/add")
    public String addContact(@ModelAttribute Contact contact, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                contact.setUser(user);
                contactService.saveContact(contact);
                
                logger.info("Contact added successfully by user: {}", email);
                redirectAttributes.addFlashAttribute("successMessage", "Contact added successfully!");
            }
        } catch (Exception e) {
            logger.error("Error adding contact: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add contact!");
        }

        return "redirect:/user/contacts";
    }

    // Show edit contact form
    @GetMapping("/edit/{contactId}")
    public String showEditContactForm(@PathVariable String contactId, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Contact> contactOptional = contactService.getContactById(contactId);
            
            if (contactOptional.isPresent()) {
                Contact contact = contactOptional.get();
                
                // Check if contact belongs to user
                if (contactService.isContactOwnedByUser(contactId, user)) {
                    model.addAttribute("user", user);
                    model.addAttribute("contact", contact);
                    model.addAttribute("isEdit", true);
                    return "user/contact-form";
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access!");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Contact not found!");
            }
        }

        return "redirect:/user/contacts";
    }

    // Process update contact
    @PostMapping("/update/{contactId}")
    public String updateContact(@PathVariable String contactId, @ModelAttribute Contact contact, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                if (contactService.isContactOwnedByUser(contactId, user)) {
                    contact.setId(contactId);
                    contact.setUser(user);
                    contactService.updateContact(contact);
                    
                    logger.info("Contact updated successfully by user: {}", email);
                    redirectAttributes.addFlashAttribute("successMessage", "Contact updated successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access!");
                }
            }
        } catch (Exception e) {
            logger.error("Error updating contact: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update contact!");
        }

        return "redirect:/user/contacts";
    }

    // View contact details
    @GetMapping("/view/{contactId}")
    public String viewContactDetails(@PathVariable String contactId, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Contact> contactOptional = contactService.getContactById(contactId);
            
            if (contactOptional.isPresent()) {
                Contact contact = contactOptional.get();
                
                if (contactService.isContactOwnedByUser(contactId, user)) {
                    model.addAttribute("contact", contact);
                    model.addAttribute("user", user);
                    return "user/contact-view";
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access!");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Contact not found!");
            }
        }

        return "redirect:/user/contacts";
    }

    // Delete contact
    @PostMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable String contactId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                if (contactService.isContactOwnedByUser(contactId, user)) {
                    contactService.deleteContact(contactId);
                    logger.info("Contact deleted successfully by user: {}", email);
                    redirectAttributes.addFlashAttribute("successMessage", "Contact deleted successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access!");
                }
            }
        } catch (Exception e) {
            logger.error("Error deleting contact: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete contact!");
        }

        return "redirect:/user/contacts";
    }

    // Toggle favorite
    @PostMapping("/favorite/{contactId}")
    public String toggleFavorite(@PathVariable String contactId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                if (contactService.isContactOwnedByUser(contactId, user)) {
                    contactService.toggleFavorite(contactId);
                    logger.info("Contact favorite toggled by user: {}", email);
                    redirectAttributes.addFlashAttribute("successMessage", "Favorite status updated!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access!");
                }
            }
        } catch (Exception e) {
            logger.error("Error toggling favorite: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update favorite status!");
        }

        return "redirect:/user/contacts";
    }
}
