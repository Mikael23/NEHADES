package com.nehades.dto;

import com.nehades.data.Companies;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;


public record CompaniesDto(
        String name,
        String description,
        LocalDateTime creationDate,
        boolean active,
        boolean isDeleted) {

    public Companies createCompany() {
        return new Companies(0, name, description, LocalDateTime.now(), active, false, new ArrayList<>());
    }
}
