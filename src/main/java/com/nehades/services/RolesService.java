package com.nehades.services;


import com.nehades.data.Roles;
import com.nehades.dto.RolesDto;
import com.nehades.repository.RolesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RolesService {
    private final RolesRepository rolesRepository;

    public RolesDto createRole(RolesDto rolesDto) {
        Roles role = rolesDto.createRole();
        role = rolesRepository.save(role);
        return new RolesDto(role.name(),role.description(),role.active());
    }
}
