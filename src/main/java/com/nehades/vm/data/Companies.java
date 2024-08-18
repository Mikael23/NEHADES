package com.nehades.vm.data;

import com.nehades.vm.dto.CompaniesDto;
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
@NoArgsConstructor
@Entity
@Getter
public class Companies {
    @Id
    private  long id;
    private  String name;
    private  String description;
    private  LocalDateTime creationDate;
    private  boolean active;
    private  boolean isDeleted;
    @OneToMany
    private  List<Projects> listOfProjects;

    public CompaniesDto createDto(){
        return new CompaniesDto(getName(),getDescription(),getCreationDate(),isActive(),isDeleted());
    }
    public Companies(String name, String description, LocalDateTime creationDate, boolean active, boolean isDeleted) {
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.active = active;
        this.isDeleted = isDeleted;
    }

}
