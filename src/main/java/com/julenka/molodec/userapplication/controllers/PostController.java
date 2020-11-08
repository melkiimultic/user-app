package com.julenka.molodec.userapplication.controllers;

import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.dtoObjects.ResponseWithErrors;
import com.julenka.molodec.userapplication.dtoObjects.ResponseWithoutErrors;
import com.julenka.molodec.userapplication.repository.UserRepository;
import com.julenka.molodec.userapplication.services.DtoUserValidatorService;
import com.julenka.molodec.userapplication.services.UserWithRolesSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DtoUserValidatorService dtoUserValidatorService;
    @Autowired
    private UserWithRolesSaverService userWithRolesSaverService;

    @PostMapping(value = "/user")
    public ResponseEntity<Object> createNewUser(@RequestBody DtoUser dtoUser) {

        Object validated = dtoUserValidatorService.validate(dtoUser);
        if (validated instanceof ResponseWithoutErrors) {
            if (userRepository.existsById(dtoUser.getLogin())) {
                ResponseWithErrors error = new ResponseWithErrors();
                error.setErrors(List.of("this user already exists"));
                return ResponseEntity.status(400).body(error);
            }
           userWithRolesSaverService.saveUserWithRoles(dtoUser);
            return ResponseEntity.status(200).body(validated);
        }
        return ResponseEntity.status(400).body(validated);
    }

}
