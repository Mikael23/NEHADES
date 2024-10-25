package com.nehades.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public record Hservices(
        @Id
        Long id,
        String name,
        String description) {


    public Hservices(String name, String description) {
        this(null, name, description);



    }
}
