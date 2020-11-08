package com.julenka.molodec.userapplication.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.julenka.molodec.userapplication.domain.User;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.Collections;

import static com.julenka.molodec.userapplication.EnumRoles.ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    UserWithRolesSaverService userWithRolesSaverService;

    @Test
    @Transactional
    @SneakyThrows
    @DisplayName("Create valid user ->status 200")
    public void responseIsSuccess() {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setLogin("login");
        dtoUser.setName("name");
        dtoUser.setPassword("Password1");
        dtoUser.setRoles(Collections.singletonList(ADMIN));
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoUser)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode response = mapper.readTree(mvcResult.getResponse().getContentAsString());
        assertTrue(response.get("success").asBoolean());
        //user
        User user = userRepository.findById("login").get();
        assertEquals("login", user.getLogin());
        assertEquals("Password1", user.getPassword());
        assertEquals("name", user.getName());
        assertEquals(1, user.getRoles().size());
        System.out.println(user.getRoles().get(0).getId());
        //role
//        List<Role> roles = roleRepository.findAll();
//        assertEquals(1, roles.size());
//        assertEquals(ADMIN, roles.get(0).getName());
   //     assertEquals("login", roles.get(0).getUser().getLogin());
    }

    @Test
    @SneakyThrows
    @DisplayName("Empty login ->status 400")
    public void wrongFormatLogin() {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setLogin("");
        dtoUser.setName("name");
        dtoUser.setPassword("Password1");
        dtoUser.setRoles(Collections.singletonList(ADMIN));
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoUser)))
                .andExpect(status().isBadRequest())
                .andReturn();
        JsonNode response = mapper.readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(response.get("success").asBoolean());
    }

    @Test
    @SneakyThrows
    @DisplayName("Empty name ->status 400")
    public void wrongFormatName() {

        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ClassPathResource("fixtures/userWithEmptyName.json").getInputStream().readAllBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();
        JsonNode response = new ObjectMapper().readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(response.get("success").asBoolean());
    }

    @Test
    @SneakyThrows
    @DisplayName("Password without at least 1 capital ->status 400")
    public void wrongFormatPassword() {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setLogin("");
        dtoUser.setName("name");
        dtoUser.setPassword("ssword1");
        dtoUser.setRoles(Collections.singletonList(ADMIN));
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoUser)))
                .andExpect(status().isBadRequest())
                .andReturn();
        JsonNode response = mapper.readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(response.get("success").asBoolean());
    }

    @Test
    @SneakyThrows
    @DisplayName("Password without at least 1 numeral ->status 400")
    public void wrongFormatPassword2() {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setLogin("");
        dtoUser.setName("name");
        dtoUser.setPassword("Password");
        dtoUser.setRoles(Collections.singletonList(ADMIN));
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoUser)))
                .andExpect(status().isBadRequest())
                .andReturn();
        JsonNode response = mapper.readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(response.get("success").asBoolean());
    }

    @Test
    @SneakyThrows
    @DisplayName("Empty list of roles ->status 400")
    public void emptyRoles() {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setLogin("login");
        dtoUser.setName("name");
        dtoUser.setPassword("Password1");
        dtoUser.setRoles(Collections.EMPTY_LIST);
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoUser)))
                .andExpect(status().isBadRequest())
                .andReturn();
        JsonNode response = mapper.readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(response.get("success").asBoolean());
    }

    @Test
    @SneakyThrows
    @DisplayName("create user with the same login as existed one ->status 400")
    public void createUserWithTheSameLogin() {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setLogin("login");
        dtoUser.setName("name");
        dtoUser.setPassword("Password1");
        dtoUser.setRoles(Collections.singletonList(ADMIN));
        userWithRolesSaverService.saveUserWithRoles(dtoUser);
        DtoUser dtoUser2 = new DtoUser();
        dtoUser2.setLogin("login");
        dtoUser2.setName("name2");
        dtoUser2.setPassword("Password2");
        dtoUser2.setRoles(Collections.singletonList(ADMIN));
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoUser2)))
                .andExpect(status().isBadRequest())
                .andReturn();
        JsonNode response = mapper.readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(response.get("success").asBoolean());

    }
    @BeforeEach
    void deleteAllUsers(){
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }
}