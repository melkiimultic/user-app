package com.julenka.molodec.userapplication.controllers;

import com.julenka.molodec.userapplication.domain.User;
import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.dtoObjects.ResponseWithErrors;
import com.julenka.molodec.userapplication.repository.RoleRepository;
import com.julenka.molodec.userapplication.repository.UserRepository;
import com.julenka.molodec.userapplication.services.DtoUserValidatorService;
import com.julenka.molodec.userapplication.services.DtoUserWithoutRolesValidatorService;
import com.julenka.molodec.userapplication.services.RoleSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Optional;

@RestController
public class PutController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DtoUserWithoutRolesValidatorService dtoUserWithoutRolesValidatorService;
    @Autowired
    private DtoUserValidatorService dtoUserValidatorService;
    @Autowired
    private RoleSaverService roleSaverService;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    @PutMapping(value = "/user")
    public ResponseEntity<Object> editUser(@RequestBody DtoUser dtoUser) {
        Optional<User> userOpt = userRepository.findById(dtoUser.getLogin());
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(404).build();
        }
        User user = userOpt.get();
        if (dtoUser.getRoles()== null) {
            Object validated = dtoUserWithoutRolesValidatorService.validate(dtoUser);
            if (validated instanceof ResponseWithErrors) {
                return ResponseEntity.status(400).body(validated);
            }
            user.setPassword(dtoUser.getPassword());
            user.setName(dtoUser.getName());

            return ResponseEntity.status(200).body(validated);
        }
        Object validated = dtoUserValidatorService.validate(dtoUser);
        if (validated instanceof ResponseWithErrors) {
            return ResponseEntity.status(400).body(validated);
        }
        user.setPassword(dtoUser.getPassword());
        user.setName(dtoUser.getName());
        roleRepository.deleteAll(roleRepository.findAllByUser(user));
        roleSaverService.saveRole(dtoUser, user);
        return ResponseEntity.status(200).body(validated);
    }
}
