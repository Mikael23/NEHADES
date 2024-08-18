package com.nehades.vm.controller;

import com.nehades.vm.dto.UsersDto;
import com.nehades.vm.services.UsersServices;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/user")
@AllArgsConstructor
public class UserController {

    private final UsersServices usersServices;

    @PostMapping
    public ResponseEntity<UsersDto> createUser(UsersDto usersDto){
        return ResponseEntity.ok().body(usersServices.createUser(usersDto));
    }

    @PutMapping("/add-project")
    public ResponseEntity<UsersDto>addProjects(@RequestParam(name = "projectId")long projectId,@RequestParam(name = "userId")long userId) throws Exception {
        return ResponseEntity.ok().body(usersServices.addProjectToUser(projectId, userId));
    }

    @PutMapping("/add-role")
    public ResponseEntity<UsersDto>addRole(@RequestParam(name = "roleId")long roleId,@RequestParam(name = "userId")long userId) throws Exception {
        return ResponseEntity.ok().body(usersServices.addRoleToUser(roleId, userId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UsersDto>getUserById(@PathVariable long id) throws Exception {
        return ResponseEntity.ok().body(usersServices.getUserById(id));
    }
    @GetMapping
    public ResponseEntity<List<UsersDto>> getAllusers(){
        return ResponseEntity.ok().body(usersServices.getAllUsers());
    }

}
