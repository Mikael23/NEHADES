package com.nehades.vm.services;

import com.nehades.vm.data.Hservices;
import com.nehades.vm.data.Projects;
import com.nehades.vm.dto.HservicesDto;
import com.nehades.vm.dto.ProjectsDto;
import com.nehades.vm.repository.HservicesRepository;
import com.nehades.vm.repository.ProjectsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HserviceServices {
    private final HservicesRepository hservicesRepository;
    public HservicesDto createService(HservicesDto hservicesDto){
        Hservices hservice = hservicesDto.createHservice();
        Hservices hserviceAfterSave = hservicesRepository.save(hservice);
        return new HservicesDto(hserviceAfterSave.getName(),hserviceAfterSave.getDescription());
    }
}
