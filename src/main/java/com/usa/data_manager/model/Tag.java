package com.usa.data_manager.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String name;
    
    // Many-to-many relationship with Contact
    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();
}
