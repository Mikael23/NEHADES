package com.nehades.vm.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Entity
@NoArgsConstructor
@Getter
public class Roles {
    @Id
    private long id;
    private String name;
    private String description;
    private boolean active;

    public Roles(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }
}
