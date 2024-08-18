package com.nehades.vm.dto;

import com.nehades.vm.data.Hservices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HservicesDto {
    private String name;
    private String description;

    public Hservices createHservice(){
        return new Hservices(name,description);
    }
}
