package com.nehades.dto;

import com.nehades.data.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public record UsersDto(
        long id,
        String firstName,
        String lastName,
        String email,
        String password,
        String description,
        boolean active,
        boolean isDeleted,
        LocalDate registrationDate) {

    public Users createUser() {
        return new Users(firstName, lastName, email, password, description, active, isDeleted, LocalDate.now());
    }
}
