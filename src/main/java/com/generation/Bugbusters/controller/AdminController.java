package com.generation.Bugbusters.controller;

import com.generation.Bugbusters.dto.AdminUserViewDTO;
import com.generation.Bugbusters.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
// protegge l'intero controller: solo chi ha ROLE_ADMIN può entrare
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * endpoint per VEDERE TUTTI gli utenti.
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserViewDTO>> getAllUsers() {
        List<AdminUserViewDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * endpoint per VEDERE SOLO i player
     * GET /api/admin/users/players
     */
    @GetMapping("/users/players")
    public ResponseEntity<List<AdminUserViewDTO>> getPlayersOnly() {
        List<AdminUserViewDTO> users = adminService.getPlayersOnly();
        return ResponseEntity.ok(users);
    }

    /**
     * endpoint per VEDERE SOLO i master
     * GET /api/admin/users/masters
     */
    @GetMapping("/users/masters")
    public ResponseEntity<List<AdminUserViewDTO>> getMastersOnly() {
        List<AdminUserViewDTO> users = adminService.getMastersOnly();
        return ResponseEntity.ok(users);
    }

    /**
     * endpoint per bannare (sospendere) un utente
     * POST /api/admin/users/{id}/ban
     */
    @PostMapping("/users/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        
        // delega tutta la logica (ban utente + gestione campagne) al service
        return adminService.banUser(id);
    }
}