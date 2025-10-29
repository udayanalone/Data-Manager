package com.usa.data_manager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    private String link;
    
    // Many-to-one relationship with Contact
    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;
}
