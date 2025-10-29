package com.usa.data_manager.jpa;

import com.usa.data_manager.model.Contact;
import com.usa.data_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
    
    List<Contact> findByUser(User user);
    
    List<Contact> findByUserUserId(String userId);
    
    List<Contact> findByUserAndFavorite(User user, boolean favorite);
    
    @Query("SELECT c FROM Contact c WHERE c.user = :user AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Contact> searchByName(@Param("user") User user, @Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT c FROM Contact c LEFT JOIN FETCH c.links WHERE c.user = :user")
    List<Contact> findByUserWithSocialLinks(@Param("user") User user);
    
    @Query("SELECT DISTINCT c FROM Contact c LEFT JOIN FETCH c.tags WHERE c.user = :user")
    List<Contact> findByUserWithTags(@Param("user") User user);
    
    @Query("SELECT c FROM Contact c WHERE c.id IN :ids AND c.user = :user")
    List<Contact> findAllByIdInAndUser(@Param("ids") List<String> ids, @Param("user") User user);
    
    boolean existsByIdAndUser(String id, User user);
    
    long countByUser(User user);
}
