package com.nehades.dto;

import com.nehades.data.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record RolesDto(
        String name,
        String description,
        boolean active
) {
    public Roles createRole() {
        return new Roles(name, description, active);
    }
}
