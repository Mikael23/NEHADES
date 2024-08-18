package com.nehades.vm.controller;

import com.nehades.vm.dto.ProjectsDto;
import com.nehades.vm.services.ProjectServices;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("projects")
@AllArgsConstructor
public class ProjectsController {

    private final ProjectServices projectServices;

    @PostMapping
    public ResponseEntity<ProjectsDto>createProject(@RequestBody ProjectsDto projectsDto){
        return ResponseEntity.ok().body(projectServices.createProject(projectsDto));
    }
    @PutMapping("/add-service")
    public ProjectsDto assignProjectToService(@RequestParam(name = "projectId")long projectId, @RequestParam (name = "serviceId")long service) throws Exception {
        return projectServices.addServiceToProject(service,projectId);
    }
}
