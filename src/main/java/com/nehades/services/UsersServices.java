package com.nehades.services;


import com.nehades.data.Projects;
import com.nehades.data.Roles;
import com.nehades.data.Users;
import com.nehades.dto.UsersDto;
import com.nehades.repository.ProjectsRepository;
import com.nehades.repository.RolesRepository;
import com.nehades.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UsersServices {
    private final UsersRepository usersRepository;
    private final ProjectsRepository projectsRepository;
    private final RolesRepository rolesRepository;

    public UsersDto createUser(UsersDto usersDto) {
        Users user = usersDto.createUser();
        Users userAfterSave = usersRepository.save(user);
        return new UsersDto(userAfterSave.id(), userAfterSave.firstName(), userAfterSave.lastName(), userAfterSave.email(), userAfterSave.password(), userAfterSave.description(), userAfterSave.active(), userAfterSave.isDeleted(), userAfterSave.registrationDate());
    }

    public UsersDto addProjectToUser(long projectId, long userId) throws Exception {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        Projects project = projectsRepository.findById(projectId).orElseThrow(() -> new Exception("Project not found"));
        user.projects().add(project);
        user = usersRepository.save(user);
        return new UsersDto(user.id(), user.firstName(), user.lastName(), user.email(), user.password(), user.description(), user.active(), user.isDeleted(), user.registrationDate());
    }

    public UsersDto addRoleToUser(long roleId, long userId) throws Exception {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        Roles role = rolesRepository.findById(roleId).orElseThrow(() -> new Exception("Role not found"));
        user.hroles().add(role);
        user = usersRepository.save(user);
        return new UsersDto(user.id(), user.firstName(), user.lastName(), user.email(), user.password(), user.description(), user.active(), user.isDeleted(), user.registrationDate());
    }

    public UsersDto getUserById(long userId) throws Exception {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new Exception("user not found"));
        return new UsersDto(user.id(), user.firstName(), user.lastName(), user.email(), user.password(), user.description(), user.active(), user.isDeleted(), user.registrationDate());
    }

    public List<UsersDto> getAllUsers() {
        return usersRepository.findAll().stream().map(user -> {
            return new UsersDto(user.id(), user.firstName(), user.lastName(), user.email(), user.password(), user.description(), user.active(), user.isDeleted(), user.registrationDate());
        }).collect(Collectors.toList());
    }
}
