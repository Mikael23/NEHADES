package com.nehades.services;


import com.nehades.data.Hservices;
import com.nehades.data.Projects;
import com.nehades.dto.ProjectsDto;
import com.nehades.repository.HservicesRepository;
import com.nehades.repository.ProjectsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProjectServices {

    private final ProjectsRepository projectsRepository;
    private final HservicesRepository hservicesRepository;

    public ProjectsDto createProject(ProjectsDto projectsDto) {
        Projects project = projectsDto.createProject();
        Projects projectAfterSave = projectsRepository.save(project);
        return new ProjectsDto(projectAfterSave.description(), projectAfterSave.active(), projectAfterSave.isDeleted(), projectAfterSave.creationDate(), projectAfterSave.listOfServices());
    }
    public ProjectsDto addServiceToProject(long serviceId, long projectId) throws Exception {
        Projects project = projectsRepository.findById(projectId).orElseThrow(() -> new Exception("Project not found"));
        Hservices service = hservicesRepository.findById(serviceId).orElseThrow(()->new Exception("Project not found"));
        project.listOfServices().add(service);
        Projects projectAfterSave = projectsRepository.save(project);
        return projectAfterSave.createProject();
    }
}
