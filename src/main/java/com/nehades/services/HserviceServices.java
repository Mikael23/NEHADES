package com.nehades.services;


import com.nehades.data.Hservices;
import com.nehades.dto.HservicesDto;
import com.nehades.repository.HservicesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HserviceServices {
    private final HservicesRepository hservicesRepository;
    public HservicesDto createService(HservicesDto hservicesDto){
        Hservices hservice = hservicesDto.createHservice();
        Hservices hserviceAfterSave = hservicesRepository.save(hservice);
        return new HservicesDto(hserviceAfterSave.name(),hserviceAfterSave.description());
    }
}
