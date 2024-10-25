package com.nehades.dto;


import com.nehades.data.Hservices;
import com.nehades.data.Projects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


public record ProjectsDto(
        String description,
        boolean active,
        boolean isDeleted,
        LocalDateTime creationDate,
        List<Hservices> listOfServices) {

    public Projects createProject() {
        return new Projects(description, active, false, LocalDateTime.now());
    }
}
