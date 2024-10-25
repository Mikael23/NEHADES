package com.nehades.services;


import com.nehades.data.Companies;
import com.nehades.data.Projects;
import com.nehades.dto.CompaniesDto;
import com.nehades.repository.CompanyRepository;
import com.nehades.repository.ProjectsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


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
        company.listOfProjects().add(project);
        Companies companyAfterSave = companyRepository.save(company);
        return companyAfterSave.createDto();
    }
}
