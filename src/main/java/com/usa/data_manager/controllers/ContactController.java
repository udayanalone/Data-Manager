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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.usa.data_manager.dto.ContactExportDTO;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

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

    @PostMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable String contactId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                if (contactService.isContactOwnedByUser(contactId, user)) {
                    if (contactService.deleteContact(contactId)) {
                        logger.info("Contact deleted successfully by user: {}", email);
                        redirectAttributes.addFlashAttribute("successMessage", "Contact deleted successfully!");
                    } else {
                        logger.warn("Failed to delete contact with ID: {}", contactId);
                        redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete contact. Please try again.");
                    }
                } else {
                    logger.warn("Unauthorized delete attempt for contact ID: {} by user: {}", contactId, email);
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access!");
                }
            }
        } catch (Exception e) {
            logger.error("Error deleting contact: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete contact!");
        }

        return "redirect:/user/contacts";
    }

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

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportContacts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isEmpty()) {
                logger.warn("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            User user = userOptional.get();
            logger.info("Exporting contacts for user: {}", user.getEmail());
            
            // Fetch contacts with social links in a single query to avoid lazy loading issues
            List<Contact> contacts = contactService.getContactsWithSocialLinksByUser(user);
            
            // Convert to DTOs for export
            List<ContactExportDTO> exportData = contacts.stream()
                .map(ContactExportDTO::fromEntity)
                .collect(Collectors.toList());
            
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 Writer writer = new OutputStreamWriter(out)) {
                
                StatefulBeanToCsv<ContactExportDTO> csvWriter = new StatefulBeanToCsvBuilder<ContactExportDTO>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(',')
                    .withOrderedResults(true)
                    .build();
                
                csvWriter.write(exportData);
                writer.flush();
                
                byte[] csvBytes = out.toByteArray();
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "contacts_export.csv");
                
                logger.info("Successfully exported {} contacts for user: {}", exportData.size(), user.getEmail());
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);
            }
        } catch (Exception e) {
            logger.error("Error exporting contacts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/import")
    public String showImportPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "user/contact-import";
        }
        return "redirect:/loginPage";
    }

    @PostMapping("/import")
    public String importContacts(
        @RequestParam("file") MultipartFile file,
        RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            
            User user = userOptional.get();
            
            try (Reader reader = new InputStreamReader(file.getInputStream());
                 CSVReader csvReader = new CSVReader(reader)) {
                
                // Skip header
                String[] header = csvReader.readNext();
                if (header == null) {
                    throw new RuntimeException("Empty CSV file");
                }
                
                String[] line;
                int successCount = 0;
                
                while ((line = csvReader.readNext()) != null) {
                    try {
                        Contact contact = new Contact();
                        contact.setId(UUID.randomUUID().toString());
                        contact.setUser(user);
                        
                        // Map CSV columns to Contact fields
                        if (line.length > 0) contact.setName(line[0]);  // name
                        if (line.length > 1) contact.setEmail(line[1]);  // email
                        if (line.length > 2) contact.setPhoneNumber(line[2]);  // phoneNumber
                        if (line.length > 3) contact.setAddress(line[3]);  // address
                        if (line.length > 4) contact.setDescription(line[4]);  // description
                        if (line.length > 5) contact.setWebsiteLink(line[5]);  // websiteLink
                        if (line.length > 6) contact.setLinkedInLink(line[6]);  // linkedInLink
                        if (line.length > 7) contact.setFavorite(Boolean.parseBoolean(line[7]));  // favorite
                        
                        contactService.saveContact(contact);
                        successCount++;
                        
                    } catch (Exception e) {
                        logger.error("Error importing contact: " + String.join(",", line), e);
                    }
                }
                
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Successfully imported " + successCount + " contacts");
                    
            } catch (IOException e) {
                logger.error("Error reading CSV file", e);
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Error reading CSV file: " + e.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("Error during contact import", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error importing contacts: " + e.getMessage());
        }
        
        return "redirect:/user/contacts/import";
    }
    
    @PostMapping("/bulk")
    public String handleBulkActions(
            @RequestParam("action") String action,
            @RequestParam("contactIds") List<String> contactIds,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            logger.info("Processing bulk action '{}' for contact IDs: {}", action, contactIds);
            
            switch (action.toLowerCase()) {
                case "favorite":
                    contactService.markAsFavorite(contactIds, user, true);
                    redirectAttributes.addFlashAttribute("successMessage", "Selected contacts marked as favorite");
                    break;
                case "unfavorite":
                    contactService.markAsFavorite(contactIds, user, false);
                    redirectAttributes.addFlashAttribute("successMessage", "Selected contacts removed from favorites");
                    break;
                case "delete":
                    contactService.deleteContacts(contactIds, user);
                    redirectAttributes.addFlashAttribute("successMessage", "Selected contacts deleted successfully");
                    break;
                default:
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid bulk action");
                    return "redirect:/user/contacts";
            }
            
            return "redirect:/user/contacts";
            
        } catch (Exception e) {
            logger.error("Error performing bulk action: " + action, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error performing bulk action: " + e.getMessage());
            return "redirect:/user/contacts";
        }
    }
}
