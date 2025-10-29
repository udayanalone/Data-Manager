package com.usa.data_manager.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {
    
    @Id
    private String id;
    
    private String name;
    
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    private String address;
    
    @Column(length = 1000)
    private String picture;
    
    @Column(length = 1000)
    private String description;
    
    private boolean favorite;
    
    @Column(name = "website_link")
    private String websiteLink;
    
    @Column(name = "linkedin_link")
    private String linkedInLink;
    
    // Many-to-one relationship with User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // One-to-many relationship with SocialLink
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private List<SocialLink> links = new ArrayList<>();
    
    // Many-to-many relationship with Tag
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "contact_tags",
        joinColumns = @JoinColumn(name = "contact_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();
    
    // One-to-many relationship with Reminder
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();
}
