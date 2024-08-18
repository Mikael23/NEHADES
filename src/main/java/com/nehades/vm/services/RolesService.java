package com.nehades.vm.services;

import com.nehades.vm.data.Roles;
import com.nehades.vm.dto.RolesDto;
import com.nehades.vm.repository.RolesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RolesService {
    private final RolesRepository rolesRepository;

    public RolesDto createRole(RolesDto rolesDto) {
        Roles role = rolesDto.createRole();
        role = rolesRepository.save(role);
        return new RolesDto(role.getName(),role.getDescription(),role.isActive());
    }
}
