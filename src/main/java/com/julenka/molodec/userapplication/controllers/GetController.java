package com.julenka.molodec.userapplication.controllers;

import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.dtoObjects.UserWithoutRoles;
import com.julenka.molodec.userapplication.services.UserToDtoUserMappingService;
import com.julenka.molodec.userapplication.services.UserToUserWithoutRolesMapperService;
import com.julenka.molodec.userapplication.domain.User;
import com.julenka.molodec.userapplication.exceptions.NoSuchEntityException;
import com.julenka.molodec.userapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class GetController {
    @Autowired
    private UserRepository users;
    @Autowired
    private UserToDtoUserMappingService userToDtoUserMappingService;
    @Autowired
    private UserToUserWithoutRolesMapperService userToUserWithoutRolesMapperService;

    @GetMapping(path = "/users")
    public ResponseEntity<List<UserWithoutRoles>> getAllUsers() {
        List<UserWithoutRoles> allUsers = new ArrayList<>();
        if (users.count() == 0) {
            return ResponseEntity.status(400).build();
        }
        for (User user : users.findAll()) {
            allUsers.add(userToUserWithoutRolesMapperService.mapUserToUserWithoutRoles(user));
        }
        return ResponseEntity.status(200).body(allUsers);
    }


    @GetMapping(path = "/user/{login}")
    public DtoUser getAUser(@PathVariable("login") String login) {

        Optional<User> byId = users.findById(login);
        if (!byId.isPresent()) {
            throw new NoSuchEntityException();
        }
        DtoUser thisDtoUser = userToDtoUserMappingService.mapUserToDtoUser(byId.get());
        return thisDtoUser;

    }
}