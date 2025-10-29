package com.usa.data_manager.services;

import com.usa.data_manager.jpa.ContactRepository;
import com.usa.data_manager.model.Contact;
import com.usa.data_manager.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    private ContactRepository contactRepository;

    public Contact saveContact(Contact contact) {
        if (contact.getId() == null || contact.getId().isEmpty()) {
            contact.setId(UUID.randomUUID().toString());
        }
        
        logger.info("Saving contact: {}", contact.getName());
        return contactRepository.save(contact);
    }

    public Optional<Contact> getContactById(String id) {
        return contactRepository.findById(id);
    }

    public List<Contact> getContactsByUser(User user) {
        return contactRepository.findByUser(user);
    }

    public List<Contact> getContactsByUserId(String userId) {
        return contactRepository.findByUserUserId(userId);
    }

    public List<Contact> getFavoriteContacts(User user) {
        return contactRepository.findByUserAndFavorite(user, true);
    }

    public List<Contact> searchContacts(User user, String keyword) {
        return contactRepository.searchByName(user, keyword);
    }

    public Contact updateContact(Contact contact) {
        logger.info("Updating contact with ID: {}", contact.getId());
        return contactRepository.save(contact);
    }

    public void deleteContact(String contactId) {
        logger.info("Deleting contact with ID: {}", contactId);
        contactRepository.deleteById(contactId);
    }

    public Contact toggleFavorite(String contactId) {
        Optional<Contact> contactOptional = getContactById(contactId);
        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            contact.setFavorite(!contact.isFavorite());
            return updateContact(contact);
        }
        return null;
    }

    public long countContactsByUser(User user) {
        return contactRepository.countByUser(user);
    }

    public boolean isContactOwnedByUser(String contactId, User user) {
        Optional<Contact> contactOptional = getContactById(contactId);
        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            return contact.getUser().getUserId().equals(user.getUserId());
        }
        return false;
    }
}
