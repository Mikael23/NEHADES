package com.nehades.vm.services;

import com.nehades.vm.data.Companies;
import com.nehades.vm.data.Projects;
import com.nehades.vm.dto.CompaniesDto;
import com.nehades.vm.repository.CompanyRepository;
import com.nehades.vm.repository.ProjectsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final ProjectsRepository projectsRepository;
    public CompaniesDto createCompany(CompaniesDto companiesDto){
        Companies company = companiesDto.createCompany();
        Companies companyAfterSave = companyRepository.save(company);
        return companyAfterSave.createDto();
    }
    public CompaniesDto addProjectToCompany(long companyId,long projectId) throws Exception {
        Companies company = companyRepository.findById(companyId).orElseThrow(()->new Exception("Company not found"));
        Projects project = projectsRepository.findById(projectId).orElseThrow(()->new Exception("Project not found"));
        company.getListOfProjects().add(project);
        Companies companyAfterSave = companyRepository.save(company);
        return companyAfterSave.createDto();
    }
}
