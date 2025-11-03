package com.generation.Bugbusters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller per gestire le pagine HTML statiche dell'applicazione
 * Responsabile del routing delle view (non API endpoints)
 */
@Controller
public class PageController {

    /**
     * Pagina principale - Redirect alla Landing Page
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/landing.html";
    }

    /**
     * Landing Page esplicita
     */
    @GetMapping("/landing")
    public String landing() {
        return "redirect:/landing.html";
    }

    /**
     * Pagina di registrazione
     */
    @GetMapping("/signup")
    public String signup() {
        return "redirect:/register.html";
    }

    /**
     * Dashboard Master (protetta da Spring Security)
     */
    @GetMapping("/dashboard/master")
    public String dashboardMaster() {
        return "redirect:/master.html";
    }

    /**
     * Dashboard Player (protetta da Spring Security)
     */
    @GetMapping("/dashboard/player")
    public String dashboardPlayer() {
        return "redirect:/player.html";
    }

    /**
     * Pagina Admin (protetta da Spring Security - solo ADMIN)
     */
    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin.html";
    }

    /**
     * Pagina Profilo (protetta da Spring Security)
     */
    @GetMapping("/profile")
    public String profile() {
        return "redirect:/profile.html";
    }
}