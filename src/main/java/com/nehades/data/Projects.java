package com.nehades.data;

import com.nehades.dto.ProjectsDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public record Projects(
        @Id
        long id,
        String description,
        boolean active,
        boolean isDeleted,
        LocalDateTime creationDate,
        @OneToMany
        List<Hservices> listOfServices) {

    public ProjectsDto createProject() {
        return new ProjectsDto(description, active, isDeleted, creationDate, listOfServices);
    }

    public Projects(String description, boolean active,
                    boolean isDeleted, LocalDateTime creationDate) {
        this(0, description, active, isDeleted, creationDate, new ArrayList<>());
    }
}
