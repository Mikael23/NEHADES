package com.nehades.repository;

import com.nehades.data.Companies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Companies,Long> {
}
