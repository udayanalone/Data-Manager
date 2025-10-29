package com.usa.data_manager.dto;

import com.opencsv.bean.CsvBindByName;
import com.usa.data_manager.model.SocialLink;

import java.util.List;
import java.util.stream.Collectors;

public class ContactExportDTO {
    @CsvBindByName(column = "Name")
    private String name;
    
    @CsvBindByName(column = "Email")
    private String email;
    
    @CsvBindByName(column = "Phone")
    private String phoneNumber;
    
    @CsvBindByName(column = "Address")
    private String address;
    
    @CsvBindByName(column = "Description")
    private String description;
    
    @CsvBindByName(column = "Website")
    private String websiteLink;
    
    @CsvBindByName(column = "LinkedIn")
    private String linkedInLink;
    
    @CsvBindByName(column = "Social Links")
    private String socialLinks;
    
    @CsvBindByName(column = "Tags")
    private String tags;
    
    @CsvBindByName(column = "Favorite")
    private boolean favorite;
    
    public ContactExportDTO() {
        // Default constructor for OpenCSV
    }
    
    public static ContactExportDTO fromEntity(com.usa.data_manager.model.Contact contact) {
        ContactExportDTO dto = new ContactExportDTO();
        dto.setName(contact.getName());
        dto.setEmail(contact.getEmail());
        dto.setPhoneNumber(contact.getPhoneNumber());
        dto.setAddress(contact.getAddress());
        dto.setDescription(contact.getDescription());
        dto.setWebsiteLink(contact.getWebsiteLink());
        dto.setLinkedInLink(contact.getLinkedInLink());
        dto.setFavorite(contact.isFavorite());
        
        // Convert social links to a comma-separated string
        if (contact.getLinks() != null) {
            String links = contact.getLinks().stream()
                .map(link -> String.format("%s: %s", link.getTitle(), link.getLink()))
                .collect(Collectors.joining(", "));
            dto.setSocialLinks(links);
        }
        
        // Convert tags to a comma-separated string
        if (contact.getTags() != null) {
            String tagList = contact.getTags().stream()
                .map(tag -> tag.getName())
                .collect(Collectors.joining(", "));
            dto.setTags(tagList);
        }
        
        return dto;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getWebsiteLink() { return websiteLink; }
    public void setWebsiteLink(String websiteLink) { this.websiteLink = websiteLink; }
    
    public String getLinkedInLink() { return linkedInLink; }
    public void setLinkedInLink(String linkedInLink) { this.linkedInLink = linkedInLink; }
    
    public String getSocialLinks() { return socialLinks; }
    public void setSocialLinks(String socialLinks) { this.socialLinks = socialLinks; }
    
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
