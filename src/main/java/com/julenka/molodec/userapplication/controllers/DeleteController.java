package com.julenka.molodec.userapplication.controllers;

import com.julenka.molodec.userapplication.domain.User;
import com.julenka.molodec.userapplication.repository.UserRepository;
import com.julenka.molodec.userapplication.services.DeleteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Optional;

@RestController
public class DeleteController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeleteUserService deleteUserService;

    @Transactional
    @DeleteMapping(path = "/user/{login}")
    public ResponseEntity<Object> deleteUser(@PathVariable("login") String login) {
        Optional<User> userOpt = userRepository.findById(login);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(404).build();
        }
        User user = userOpt.get();
        deleteUserService.deleteUser(user);
        return ResponseEntity.status(200).build();
    }

}
