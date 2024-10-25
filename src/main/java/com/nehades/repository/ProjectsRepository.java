package com.nehades.repository;

import com.nehades.data.Projects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectsRepository extends JpaRepository<Projects,Long> {
}
