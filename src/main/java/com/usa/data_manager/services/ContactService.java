package com.usa.data_manager.services;

import com.usa.data_manager.jpa.ContactRepository;
import com.usa.data_manager.model.Contact;
import com.usa.data_manager.model.Tag;
import com.usa.data_manager.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.UUID;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

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
    
    public List<Contact> searchContacts(User user, String search) {
        return contactRepository.searchByName(user, search);
    }
    
    public List<Contact> getFavoriteContacts(User user) {
        return contactRepository.findByUserAndFavorite(user, true);
    }
    
    public long countContactsByUser(User user) {
        return contactRepository.countByUser(user);
    }
    
    @Transactional(readOnly = true)
    public List<Contact> getContactsWithSocialLinksByUser(User user) {
        // First fetch contacts with links (social links)
        List<Contact> contacts = contactRepository.findByUserWithSocialLinks(user);
        
        // Then fetch the same contacts with their tags
        List<Contact> contactsWithTags = contactRepository.findByUserWithTags(user);
        
        // Create a map of contact IDs to contacts with tags
        Map<String, Contact> contactMap = contactsWithTags.stream()
            .collect(Collectors.toMap(Contact::getId, contact -> contact));
        
        // Merge the tags into the original contacts
        contacts.forEach(contact -> {
            Contact contactWithTags = contactMap.get(contact.getId());
            if (contactWithTags != null) {
                contact.setTags(contactWithTags.getTags());
            }
        });
        
        return contacts;
    }

    public List<Contact> getContactsByUserId(String userId) {
        return contactRepository.findByUserUserId(userId);
    }

    @Transactional
    public Contact updateContact(Contact contact) {
        logger.info("Updating contact with ID: {}", contact.getId());
        return contactRepository.save(contact);
    }

    public boolean deleteContact(String contactId) {
        try {
            logger.info("Deleting contact with ID: {}", contactId);
            if (contactId == null || contactId.trim().isEmpty()) {
                logger.warn("Attempted to delete contact with null or empty ID");
                return false;
            }
            
            if (!contactRepository.existsById(contactId)) {
                logger.warn("Contact with ID {} not found for deletion", contactId);
                return false;
            }
            
            contactRepository.deleteById(contactId);
            logger.info("Successfully deleted contact with ID: {}", contactId);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting contact with ID {}: {}", contactId, e.getMessage(), e);
            return false;
        }
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

    public boolean isContactOwnedByUser(String contactId, User user) {
        try {
            Optional<Contact> contactOptional = getContactById(contactId);
            if (contactOptional.isPresent()) {
                Contact contact = contactOptional.get();
                return contact.getUser() != null 
                    && contact.getUser().getUserId() != null 
                    && contact.getUser().getUserId().equals(user.getUserId());
            }
            logger.debug("Contact not found with ID: {}", contactId);
        } catch (Exception e) {
            logger.error("Error checking contact ownership for contactId: {}, user: {} - {}", 
                contactId, user.getUserId(), e.getMessage(), e);
        }
        return false;
    }
    
    @Transactional
    public void markAsFavorite(List<String> contactIds, User user, boolean favorite) {
        List<Contact> contacts = contactRepository.findAllByIdInAndUser(contactIds, user);
        contacts.forEach(contact -> contact.setFavorite(favorite));
        contactRepository.saveAll(contacts);
    }
    
    @Transactional
    public void deleteContacts(List<String> contactIds, User user) {
        List<Contact> contacts = contactRepository.findAllByIdInAndUser(contactIds, user);
        contactRepository.deleteAll(contacts);
    }
    
    @Transactional
    public void deleteContact(Contact contact) {
        contactRepository.delete(contact);
    }
    
}
