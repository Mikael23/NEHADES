package com.nehades.vm.services;

import com.nehades.vm.data.Hservices;
import com.nehades.vm.data.Projects;
import com.nehades.vm.dto.ProjectsDto;
import com.nehades.vm.repository.HservicesRepository;
import com.nehades.vm.repository.ProjectsRepository;
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
        return new ProjectsDto(projectAfterSave.getDescription(), projectAfterSave.isActive(), projectAfterSave.isDeleted(), projectAfterSave.getCreationDate(), projectAfterSave.getListOfServices());
    }
    public ProjectsDto addServiceToProject(long serviceId, long projectId) throws Exception {
        Projects project = projectsRepository.findById(projectId).orElseThrow(() -> new Exception("Project not found"));
        Hservices service = hservicesRepository.findById(serviceId).orElseThrow(()->new Exception("Project not found"));
        project.getListOfServices().add(service);
        Projects projectAfterSave = projectsRepository.save(project);
        return projectAfterSave.createProject();
    }
}
