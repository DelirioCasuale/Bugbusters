package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.AdminUserViewDTO;
import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.mapper.UserMapper;
import com.generation.Bugbusters.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper; // iniettiamo il mapper per convertire User in AdminUserViewDTO

    /**
     * recupera TUTTI gli utenti nel sistema
     */
    @Transactional(readOnly = true) // FONDAMENTALE per il lazy loading
    public List<AdminUserViewDTO> getAllUsers() {
        
        List<User> users = userRepository.findAll();
        
        return users.stream()
                .map(userMapper::toAdminViewDTO) // usa il mapper per convertire
                .collect(Collectors.toList());
    }

    /**
     * recupera solo gli utenti che sono player
     */
    @Transactional(readOnly = true) // FONDAMENTALE per il lazy loading
    public List<AdminUserViewDTO> getPlayersOnly() {
        
        List<User> users = userRepository.findByPlayerIsNotNull();
        
        return users.stream()
                .map(userMapper::toAdminViewDTO)
                .collect(Collectors.toList());
    }

    /**
     * recupera solo gli utenti che sono master
     */
    @Transactional(readOnly = true) // FONDAMENTALE per il lazy loading
    public List<AdminUserViewDTO> getMastersOnly() {
        
        List<User> users = userRepository.findByMasterIsNotNull();
        
        return users.stream()
                .map(userMapper::toAdminViewDTO)
                .collect(Collectors.toList());
    }
}