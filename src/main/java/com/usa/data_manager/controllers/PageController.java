package com.usa.data_manager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    
    @RequestMapping("/")
    public String rootPage() {
        return "redirect:/home";
    }
    
    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("name","Welcome to Home Page");
        return "home";
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin",true);
        return "about";
    }

    @GetMapping("/service")
    public String servicesPage() {
        return "service";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact/contact";
    }
}
