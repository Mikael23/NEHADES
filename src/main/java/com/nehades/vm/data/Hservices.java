package com.nehades.vm.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Hservices {
    @Id
    private long id;
    private String name;
    private String description;
    public Hservices(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
