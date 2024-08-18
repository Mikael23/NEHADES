package com.nehades.vm.data;

import com.nehades.vm.dto.ProjectsDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Entity
@NoArgsConstructor
@Getter
public class Projects {
    @Id
    private long id;
    private String description;
    private boolean active;
    private boolean isDeleted;
    private LocalDateTime creationDate;
    @OneToMany
    private List<Hservices> listOfServices;

    public ProjectsDto createProject(){
        return new ProjectsDto(description,active,isDeleted,creationDate,listOfServices);
    }
    public Projects(String description, boolean active, boolean isDeleted, LocalDateTime creationDate) {
        this.description = description;
        this.active = active;
        this.isDeleted = isDeleted;
        this.creationDate = creationDate;
    }
}
