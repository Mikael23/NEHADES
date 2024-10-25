package com.nehades.dto;

import com.nehades.data.Hservices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record HservicesDto(
        String name,
        String description
) {
    public Hservices createHservice() {
        return new Hservices(name, description);
    }
}
