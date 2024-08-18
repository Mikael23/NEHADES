package com.nehades.vm.controller;


import com.nehades.vm.dto.HservicesDto;
import com.nehades.vm.dto.ProjectsDto;
import com.nehades.vm.services.HserviceServices;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController("/hservice")
public class HserviceController {
    private final HserviceServices hserviceServices;

    @PostMapping
    public ResponseEntity<HservicesDto> createService(@RequestBody HservicesDto hservicesDto){
        return ResponseEntity.ok().body(hserviceServices.createService(hservicesDto));
    }
}
