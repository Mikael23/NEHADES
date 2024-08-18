package com.nehades.vm.dto;

import com.nehades.vm.data.Hservices;
import com.nehades.vm.data.Projects;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectsDto {
    private String description;
    private boolean active;
    private boolean isDeleted;
    private LocalDateTime creationDate;
    private List<Hservices> listOfServices;

    public Projects createProject() {
        return new Projects(description, active, false, LocalDateTime.now());
    }
}
