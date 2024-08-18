package com.nehades.vm.services;

import com.nehades.vm.data.Projects;
import com.nehades.vm.data.Roles;
import com.nehades.vm.data.Users;
import com.nehades.vm.dto.UsersDto;
import com.nehades.vm.repository.HservicesRepository;
import com.nehades.vm.repository.ProjectsRepository;
import com.nehades.vm.repository.RolesRepository;
import com.nehades.vm.repository.UsersRepository;
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
        return new UsersDto(userAfterSave.getId(), userAfterSave.getFirstName(), userAfterSave.getLastName(), userAfterSave.getEmail(), userAfterSave.getPassword(), userAfterSave.getDescription(), userAfterSave.isActive(), userAfterSave.isDeleted(), userAfterSave.getRegistrationDate());
    }

    public UsersDto addProjectToUser(long projectId,long userId) throws Exception {
        Users user = usersRepository.findById(userId)
                .orElseThrow(()->new Exception("User not found"));
        Projects project = projectsRepository.findById(projectId).orElseThrow(() -> new Exception("Project not found"));
        user.getProjects().add(project);
        user = usersRepository.save(user);
        return new UsersDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getDescription(), user.isActive(), user.isDeleted(), user.getRegistrationDate());
    }
    public UsersDto addRoleToUser(long roleId,long userId) throws Exception {
        Users user = usersRepository.findById(userId)
                .orElseThrow(()->new Exception("User not found"));
        Roles role = rolesRepository.findById(roleId).orElseThrow(() -> new Exception("Role not found"));
        user.getHroles().add(role);
        user = usersRepository.save(user);
        return new UsersDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getDescription(), user.isActive(), user.isDeleted(), user.getRegistrationDate());
    }

    public UsersDto getUserById(long userId) throws Exception {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new Exception("user not found"));
        return new UsersDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getDescription(), user.isActive(), user.isDeleted(), user.getRegistrationDate());
    }

    public List<UsersDto>getAllUsers(){
        return usersRepository.findAll().stream().map(user->{
            return new UsersDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getDescription(), user.isActive(), user.isDeleted(), user.getRegistrationDate());
        }).collect(Collectors.toList());
    }
}
