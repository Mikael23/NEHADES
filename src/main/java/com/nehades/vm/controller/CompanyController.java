package com.nehades.vm.controller;


import com.nehades.vm.dto.CompaniesDto;
import com.nehades.vm.services.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("company")
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;


    @PostMapping
    public ResponseEntity<CompaniesDto> createCompany(CompaniesDto companiesDto){
        return ResponseEntity.ok().body(companyService.createCompany(companiesDto));
    }
    @PostMapping("/add-project")
    public ResponseEntity<CompaniesDto>addProject(@RequestParam(name = "projectId")long projectId,@RequestParam(name = "companyId")long companyId) throws Exception {
        return ResponseEntity.ok().body(companyService.addProjectToCompany(companyId,projectId));
    }
}
