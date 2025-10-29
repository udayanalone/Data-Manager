package com.usa.data_manager.jpa;

import com.usa.data_manager.model.Reminder;
import com.usa.data_manager.model.User;
import com.usa.data_manager.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserOrderByDueAtAsc(User user);
    List<Reminder> findByUserAndDoneOrderByDueAtAsc(User user, boolean done);
    List<Reminder> findByUserAndDueAtBeforeAndDoneFalse(User user, LocalDateTime before);
    List<Reminder> findByContact(Contact contact);
}
