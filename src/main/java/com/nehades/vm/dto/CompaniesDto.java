package com.nehades.vm.dto;

import com.nehades.vm.data.Companies;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompaniesDto {
    private  String name;
    private  String description;
    private LocalDateTime creationDate;
    private  boolean active;
    private  boolean isDeleted;

    public Companies createCompany(){
        return new Companies(name,description,LocalDateTime.now(),active,false);
    }
}
