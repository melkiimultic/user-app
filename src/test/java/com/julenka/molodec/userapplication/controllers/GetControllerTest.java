package com.julenka.molodec.userapplication.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.julenka.molodec.userapplication.EnumRoles;
import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.repository.RoleRepository;
import com.julenka.molodec.userapplication.repository.UserRepository;
import com.julenka.molodec.userapplication.services.UserWithRolesSaverService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    UserWithRolesSaverService userWithRolesSaverService;

    @Test
    @SneakyThrows
    @DisplayName("If no users in DB -> response status 400")
    public void noUsersInDB() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Return all users without  roles from DB")
    public void getAllUsers() {
        DtoUser dtoUser1 = new DtoUser();
        dtoUser1.setName("user1");
        dtoUser1.setLogin("user1");
        dtoUser1.setPassword("Password1");
        dtoUser1.setRoles(Collections.singletonList(EnumRoles.ADMIN));
        userWithRolesSaverService.saveUserWithRoles(dtoUser1);
        DtoUser dtoUser2 = new DtoUser();
        dtoUser2.setName("user2");
        dtoUser2.setLogin("user2");
        dtoUser2.setPassword("Password2");
        dtoUser2.setRoles(Collections.singletonList(EnumRoles.ADMIN));
        userWithRolesSaverService.saveUserWithRoles(dtoUser2);
        MvcResult mvcResult = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonNode response = new ObjectMapper().readTree(contentAsString);
        Iterator<JsonNode> iterator = response.iterator();
        JsonNode user1 = iterator.next();
        assertEquals("user1", user1.get("login").asText());
        assertEquals("user1", user1.get("name").asText());
        assertEquals("Password1", user1.get("password").asText());
        JsonNode user2 = iterator.next();
        assertEquals("user2", user2.get("login").asText());
        assertEquals("user2", user2.get("name").asText());
        assertEquals("Password2", user2.get("password").asText());
        assertFalse(iterator.hasNext());

    }

    @Test
    @SneakyThrows
    @DisplayName("Search by login returns definite user")
    public void getTheUser() {
        DtoUser dtoUser1 = new DtoUser();
        dtoUser1.setName("user");
        dtoUser1.setLogin("user1");
        dtoUser1.setPassword("Password1");
        dtoUser1.setRoles(Collections.singletonList(EnumRoles.ADMIN));
        userWithRolesSaverService.saveUserWithRoles(dtoUser1);
        MvcResult mvcResult = mockMvc.perform(get("/user/user1"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonNode responseNodeUser = new ObjectMapper().readTree(contentAsString);
        assertEquals("user",responseNodeUser.get("name").asText());
        assertEquals("user1",responseNodeUser.get("login").asText());
        assertEquals("Password1",responseNodeUser.get("password").asText());
        assertEquals("ADMIN",responseNodeUser.get("roles").iterator().next().asText());
    }
    @Test
    @SneakyThrows
    @DisplayName("If such user doesn't exist-> return 404")
    public void noSuchUserInDB(){
         MvcResult mvcResult = mockMvc.perform(get("/user/user1"))
                .andExpect(status().isNotFound())
                .andReturn();
         String contentAsString = mvcResult.getResponse().getContentAsString();
         assertEquals("Such a user doesn't exist! Try another login to continue.",contentAsString);
    }
    @BeforeEach
    void deleteAllUsers(){
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

}
