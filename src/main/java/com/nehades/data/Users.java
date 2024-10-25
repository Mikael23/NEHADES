package com.nehades.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
public record Users(
        @Id
        long id,
        String firstName,
        String lastName,
        String email,
        String password,
        String description,
        boolean active,
        boolean isDeleted,
        LocalDate registrationDate,
        @OneToMany
        List<Roles> hroles,
        @OneToMany
        List<Projects> projects) {


    public Users(String firstName, String lastName, String email, String password, String description, boolean active, boolean isDeleted, LocalDate registrationDate) {
        this(0, firstName, lastName, email, password, description, active, isDeleted, registrationDate, new ArrayList<>(), new ArrayList<>());
    }
}
