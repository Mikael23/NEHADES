package com.nehades.vm.dto;

import com.nehades.vm.data.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String description;
    private boolean active;
    private boolean isDeleted;
    private LocalDate registrationDate;

    public Users createUser() {
        return new Users(firstName,lastName,email,password,description,active,isDeleted,LocalDate.now());
    }
}
