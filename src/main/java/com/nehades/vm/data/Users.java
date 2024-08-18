package com.nehades.vm.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Entity
@NoArgsConstructor
@Getter
public class Users {
    @Id
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String description;
    private boolean active;
    private boolean isDeleted;
    private LocalDate registrationDate;
    @OneToMany
    private List<Roles> hroles;
    @OneToMany
    private List<Projects> projects = new LinkedList<>();


    public Users(String firstName, String lastName, String email, String password, String description, boolean active, boolean isDeleted, LocalDate registrationDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.description = description;
        this.active = active;
        this.isDeleted = isDeleted;
        this.registrationDate = registrationDate;
    }
}
