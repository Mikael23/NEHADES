package com.nehades.vm.dto;

import com.nehades.vm.data.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesDto {
    private String name;
    private String description;
    private boolean active;

    public Roles createRole(){
        return new Roles(name,description,active);
    }
}
