package com.usa.data_manager.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @Column(name = "user_id")
    private String userId;
    
    @Column(nullable = false, name = "user_name")
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "profile_pic", length = 1000)
    private String profilePic;
    
    @Column(length = 1000)
    private String about;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;
    
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;
    
    @Column(name = "phone_verified", nullable = false)
    @Builder.Default
    private boolean phoneVerified = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Providers provider = Providers.SELF;
    
    private String providerUserId;
    
    @Builder.Default
    private boolean emailNotifications = true;
    
    @Builder.Default
    private boolean twoFactorEnabled = false;
    
    // One-to-many relationship with Contact
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();
    
    // One-to-many relationship with Reminder
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();
}
