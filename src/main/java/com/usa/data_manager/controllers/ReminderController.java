package com.usa.data_manager.controllers;

import com.usa.data_manager.model.Contact;
import com.usa.data_manager.model.Reminder;
import com.usa.data_manager.model.User;
import com.usa.data_manager.services.ContactService;
import com.usa.data_manager.services.ReminderService;
import com.usa.data_manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/reminders")
public class ReminderController {

    @Autowired private ReminderService reminderService;
    @Autowired private UserService userService;
    @Autowired private ContactService contactService;

    @GetMapping
    public String list(@RequestParam(value = "contactId", required = false) String contactId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.getUserByEmail(email).orElse(null);
        if (user == null) return "redirect:/loginPage";

        List<Reminder> reminders = reminderService.listForUser(user);
        model.addAttribute("reminders", reminders);
        model.addAttribute("user", user);
        model.addAttribute("contactId", contactId);
        return "user/reminders";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Reminder> rOpt = reminderService.getById(id);
        if (rOpt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Reminder not found");
            return "redirect:/user/reminders";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.getUserByEmail(email).orElse(null);
        
        model.addAttribute("reminder", rOpt.get());
        model.addAttribute("user", user);
        model.addAttribute("isEdit", true);
        return "user/reminder-edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueAt,
            @RequestParam(required = false) String contactId,
            RedirectAttributes ra) {
        
        Optional<Reminder> rOpt = reminderService.getById(id);
        if (rOpt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Reminder not found");
            return "redirect:/user/reminders";
        }
        
        Reminder r = rOpt.get();
        r.setTitle(title);
        r.setDescription(description);
        r.setDueAt(dueAt);
        
        if (contactId != null && !contactId.isEmpty()) {
            Contact contact = contactService.getContactById(contactId).orElse(null);
            r.setContact(contact);
        }
        
        reminderService.update(r);
        ra.addFlashAttribute("successMessage", "Reminder updated successfully");
        return "redirect:/user/reminders";
    }

    @PostMapping("/add")
    public String add(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueAt,
            @RequestParam(required = false) String contactId,
            RedirectAttributes ra) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.getUserByEmail(email).orElse(null);
        if (user == null) return "redirect:/loginPage";

        Reminder r = new Reminder();
        r.setTitle(title);
        r.setDescription(description);
        r.setDueAt(dueAt);
        r.setUser(user);
        
        if (contactId != null && !contactId.isEmpty()) {
            Contact contact = contactService.getContactById(contactId).orElse(null);
            r.setContact(contact);
        }
        
        reminderService.create(r);
        ra.addFlashAttribute("successMessage", "Reminder added successfully");
        return "redirect:/user/reminders";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        Optional<Reminder> rOpt = reminderService.getById(id);
        if (rOpt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Reminder not found");
            return "redirect:/user/reminders";
        }
        Reminder r = rOpt.get();
        r.setDone(!r.isDone());
        reminderService.update(r);
        ra.addFlashAttribute("successMessage", "Reminder status updated");
        return "redirect:/user/reminders";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        reminderService.delete(id);
        ra.addFlashAttribute("successMessage", "Reminder deleted successfully");
        return "redirect:/user/reminders";
    }
}
