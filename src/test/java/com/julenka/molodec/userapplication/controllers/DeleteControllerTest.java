package com.julenka.molodec.userapplication.controllers;

import com.julenka.molodec.userapplication.EnumRoles;
import com.julenka.molodec.userapplication.domain.User;
import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.repository.RoleRepository;
import com.julenka.molodec.userapplication.repository.UserRepository;
import com.julenka.molodec.userapplication.services.DtoUserToUserMapperService;
import com.julenka.molodec.userapplication.services.UserWithRolesSaverService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DeleteControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserWithRolesSaverService userWithRolesSaverService;
    @Autowired
   DtoUserToUserMapperService dtoUserToUserMapperService;


    @Test
    @SneakyThrows
    @DisplayName("no user with such login found -> status 404")
    public void noSuchUserInDB(){
        mockMvc.perform(delete("/user/user1"))
                .andExpect(status().isNotFound());

    }
    @Test
    @SneakyThrows
    @DisplayName("delete user by login -> status 200, no such user and his roles in DB")
    public void userIsDeleted(){
        DtoUser dtoUser1 = new DtoUser();
        dtoUser1.setName("user1");
        dtoUser1.setLogin("user1");
        dtoUser1.setPassword("Password1");
        dtoUser1.setRoles(List.of(EnumRoles.ADMIN,EnumRoles.WORKER));
        userWithRolesSaverService.saveUserWithRoles(dtoUser1);
        mockMvc.perform(delete("/user/user1"))
                .andExpect(status().isOk());
        User user = dtoUserToUserMapperService.mapDtoUserToUser(dtoUser1);
        Optional<User> userOptional = userRepository.findById("user1");
        assertFalse(userOptional.isPresent());
        assertEquals(0,userRepository.count());
        assertTrue(roleRepository.findAllByUser(user).isEmpty());
        assertEquals(0,roleRepository.count());

    }
}
