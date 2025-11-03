package com.generation.Bugbusters.controller;

import com.generation.Bugbusters.dto.AdminUserViewDTO;
import com.generation.Bugbusters.service.AdminService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * endpoint per VEDERE TUTTI gli utenti (CON PAGINAZIONE).
     * GET /api/admin/users?page=0&size=50
     */
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserViewDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserViewDTO> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * endpoint per VEDERE SOLO i player (CON PAGINAZIONE).
     * GET /api/admin/users/players?page=0&size=50
     */
    @GetMapping("/users/players")
    public ResponseEntity<Page<AdminUserViewDTO>> getPlayersOnly(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserViewDTO> users = adminService.getPlayersOnly(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * endpoint per VEDERE SOLO i master (CON PAGINAZIONE).
     * GET /api/admin/users/masters?page=0&size=50
     */
    @GetMapping("/users/masters")
    public ResponseEntity<Page<AdminUserViewDTO>> getMastersOnly(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserViewDTO> users = adminService.getMastersOnly(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * endpoint per bannare (sospendere) un utente
     * POST /api/admin/users/{id}/ban
     */
    @PostMapping("/users/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        return adminService.banUser(id);
    }
}