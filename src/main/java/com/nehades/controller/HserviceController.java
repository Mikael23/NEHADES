package com.nehades.controller;



import com.nehades.dto.HservicesDto;
import com.nehades.services.HserviceServices;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
public class HserviceController {
    private final HserviceServices hserviceServices;

    @PostMapping("/hservice")
    public ResponseEntity<HservicesDto> createService(@RequestBody HservicesDto hservicesDto){
        return ResponseEntity.ok().body(hserviceServices.createService(hservicesDto));
    }
}
