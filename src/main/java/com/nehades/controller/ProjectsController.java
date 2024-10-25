package com.nehades.controller;


import com.nehades.dto.ProjectsDto;
import com.nehades.services.ProjectServices;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ProjectsController {

    private final ProjectServices projectServices;

    @PostMapping("/projects")
    public ResponseEntity<ProjectsDto>createProject(@RequestBody ProjectsDto projectsDto){
        return ResponseEntity.ok().body(projectServices.createProject(projectsDto));
    }
    @PutMapping("/projects-add-service")
    public ProjectsDto assignProjectToService(@RequestParam(name = "projectId")long projectId, @RequestParam (name = "serviceId")long service) throws Exception {
        return projectServices.addServiceToProject(service,projectId);
    }
}
