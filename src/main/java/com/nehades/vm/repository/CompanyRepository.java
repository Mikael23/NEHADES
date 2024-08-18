package com.nehades.vm.repository;

import com.nehades.vm.data.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Companies,Long> {
}
