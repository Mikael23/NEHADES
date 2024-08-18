package com.nehades.vm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nehades.vm.data.Roles;


public interface RolesRepository extends JpaRepository<Roles,Long> {
}
