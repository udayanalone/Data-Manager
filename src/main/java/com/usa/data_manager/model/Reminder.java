package com.usa.data_manager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "due_at")
    private LocalDateTime dueAt;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean done = false;
    
    // Many-to-one relationship with User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // Many-to-one relationship with Contact (optional)
    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;
}
