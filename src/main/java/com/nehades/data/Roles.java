package com.nehades.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
public record Roles(
        @Id
        long id,
        String name,
        String description,
        boolean active) {

    public Roles(String name, String description, boolean active) {
        this(0, name, description, active);

    }
}
