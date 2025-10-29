package com.usa.data_manager.services;

import com.usa.data_manager.jpa.ReminderRepository;
import com.usa.data_manager.model.Contact;
import com.usa.data_manager.model.Reminder;
import com.usa.data_manager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    public Reminder create(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    public Reminder update(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    public void delete(Long id) {
        reminderRepository.deleteById(id);
    }

    public Optional<Reminder> getById(Long id) {
        return reminderRepository.findById(id);
    }

    public List<Reminder> listForUser(User user) {
        return reminderRepository.findByUserOrderByDueAtAsc(user);
    }

    public List<Reminder> listPendingBefore(User user, LocalDateTime before) {
        return reminderRepository.findByUserAndDueAtBeforeAndDoneFalse(user, before);
    }

    public List<Reminder> listForContact(Contact contact) {
        return reminderRepository.findByContact(contact);
    }
}
