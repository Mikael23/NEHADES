package com.nehades.controller;


import com.nehades.dto.CompaniesDto;
import com.nehades.services.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }


    @PostMapping("/company")
    public ResponseEntity<CompaniesDto> createCompany(CompaniesDto companiesDto) {
        return ResponseEntity.ok().body(companyService.createCompany(companiesDto));
    }

    @PostMapping("/add-project")
    public ResponseEntity<CompaniesDto> addProject(@RequestParam(name = "projectId") long projectId, @RequestParam(name = "companyId") long companyId) throws Exception {
        return ResponseEntity.ok().body(companyService.addProjectToCompany(companyId, projectId));
    }
}
