package com.nehades.data;

import com.nehades.dto.CompaniesDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public record Companies(@Id
                        long id,
                        String name,
                        String description,
                        LocalDateTime creationDate,
                        boolean active,
                        boolean isDeleted,
                        @OneToMany
                        List<Projects> listOfProjects) {
    public CompaniesDto createDto() {
        return new CompaniesDto(name, description(), creationDate(), active(), isDeleted());
    }
}

//
//public CompaniesDto createDto() {
//    return new CompaniesDto(getName(), getDescription(), getCreationDate(), isActive(), isDeleted());
//}
