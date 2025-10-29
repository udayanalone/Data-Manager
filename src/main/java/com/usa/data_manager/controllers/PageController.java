package com.usa.data_manager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    
    // Root endpoint
    @RequestMapping("/")
    public String rootPage() {
        return "redirect:/home";
    }
    
    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("name","Welcome to Home Page");
        return "home";
    }

    //About page
    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin",true);
        return "about";
    }

    //Service Page
    @GetMapping("/service")
    public String servicesPage() {
        return "service";
    }

    // Note: /signup and /loginPage routes moved to AuthController for form processing

    //Contact Us
    @GetMapping("/contact")
    public String contactPage() {
        return "contact/contact";
    }
}
