package com.generation.Bugbusters.mapper;

import com.generation.Bugbusters.dto.AdminUserViewDTO;
import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.enumeration.RoleName;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * converte un'entità user in un DTO per la vista admin
     * N.B: Questo metodo si aspetta di essere chiamato da un contesto @Transactional
     * per poter accedere ai profili LAZY
     */
    public AdminUserViewDTO toAdminViewDTO(User user) {
        AdminUserViewDTO dto = new AdminUserViewDTO();
        
        // campi semplici
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBanned(user.isBanned());

        // logica per i ruoli "dinamici"
        
        // controlla il ruolo statico ADMIN
        boolean admin = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(RoleName.ROLE_ADMIN));
        dto.setAdmin(admin);

        // controlla i profili LAZY
        dto.setPlayer(user.getPlayer() != null);
        dto.setMaster(user.getMaster() != null);
        
        return dto;
    }
}
