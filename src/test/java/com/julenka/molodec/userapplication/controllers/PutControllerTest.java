package com.julenka.molodec.userapplication.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.julenka.molodec.userapplication.EnumRoles;
import com.julenka.molodec.userapplication.domain.Role;
import com.julenka.molodec.userapplication.domain.User;
import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.repository.RoleRepository;
import com.julenka.molodec.userapplication.repository.UserRepository;
import com.julenka.molodec.userapplication.services.UserWithRolesSaverService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.julenka.molodec.userapplication.EnumRoles.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PutControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserWithRolesSaverService userWithRolesSaverService;

    @BeforeEach
    public void saveUser() {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setLogin("login");
        dtoUser.setName("name");
        dtoUser.setPassword("Password1");
        dtoUser.setRoles(Collections.singletonList(ADMIN));
        userWithRolesSaverService.saveUserWithRoles(dtoUser);
    }

    @AfterEach
    void deleteAllUsers() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @SneakyThrows
    @DisplayName("If no such user in DB -> status 404")
    public void noSuchUser() {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setLogin("noSuchLogin");
        dtoUser.setName("name");
        dtoUser.setPassword("Password1");
        dtoUser.setRoles(Collections.singletonList(ADMIN));
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoUser)))
                .andExpect(status().isNotFound());
    }

    @Nested
    @DisplayName("If user has no roles")
    class WhenDtoHasNoRoles {

        @Test
        @SneakyThrows
        @DisplayName("valid user ->status 200,return ResponseWithoutErrors, user is updated but not his roles")
        public void userWithNoRoles() {
            MvcResult mvcResult = mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ClassPathResource("fixtures/validChangedUserWithNoRoles.json").getInputStream().readAllBytes()))
                    .andExpect(status().isOk())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            JsonNode response = new ObjectMapper().readTree(contentAsString);
            assertTrue(response.get("success").asBoolean());
            Optional<User> changedUserOpt = userRepository.findById("login");
            User changedUser = new User();
            if (changedUserOpt.isPresent()) {
                changedUser = changedUserOpt.get();
            }
            assertEquals("ChangedName", changedUser.getName());
            assertEquals("ChangedPassword1", changedUser.getPassword());
            assertEquals(1, userRepository.count());
            List<Role> allUserRoles = roleRepository.findAllByUser(changedUser);
            assertEquals(ADMIN, allUserRoles.iterator().next().getName());
            assertEquals(1, roleRepository.count());
        }

        @Test
        @SneakyThrows
        @DisplayName("request has empty name -> status 400, return ResponseWithErrors")
        public void wrongNameUserWithoutRoles() {
            DtoUser sameDtoUser = new DtoUser();
            sameDtoUser.setLogin("login");
            sameDtoUser.setName("");
            sameDtoUser.setPassword("ChangedPassword1");
            ObjectMapper mapper = new ObjectMapper();
            MvcResult mvcResult = mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(sameDtoUser)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            JsonNode response = new ObjectMapper().readTree(contentAsString);
            assertFalse(response.get("success").asBoolean());
        }

        @Test
        @SneakyThrows
        @DisplayName("request has password without capital letter -> status 400, return ResponseWithErrors")
        public void wrongPassword1UserWithoutRoles() {
            DtoUser sameDtoUser = new DtoUser();
            sameDtoUser.setLogin("login");
            sameDtoUser.setName("name1");
            sameDtoUser.setPassword("password1");
            ObjectMapper mapper = new ObjectMapper();
            MvcResult mvcResult = mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(sameDtoUser)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            JsonNode response = new ObjectMapper().readTree(contentAsString);
            assertFalse(response.get("success").asBoolean());
        }

        @Test
        @SneakyThrows
        @DisplayName("request has password without numeral -> status 400, return ResponseWithErrors")
        public void wrongPassword2UserWithoutRoles() {
            DtoUser sameDtoUser = new DtoUser();
            sameDtoUser.setLogin("login");
            sameDtoUser.setName("name1");
            sameDtoUser.setPassword("passwordD");
            ObjectMapper mapper = new ObjectMapper();
            MvcResult mvcResult = mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(sameDtoUser)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            JsonNode response = new ObjectMapper().readTree(contentAsString);
            assertFalse(response.get("success").asBoolean());
        }

    }

    @Nested
    @DisplayName("When DtoUser has roles")
    class DtoUserHasRoles {

        @Test
        @SneakyThrows
        @DisplayName("valid user -> status 200, return ResponseWithoutErrors, user is updated and his roles")
        public void userWithRoles() {
            DtoUser sameDtoUser = new DtoUser();
            sameDtoUser.setLogin("login");
            sameDtoUser.setName("ChangedName");
            sameDtoUser.setPassword("ChangedPassword1");
            sameDtoUser.setRoles(List.of(MANAGER, WORKER, ADMIN));
            ObjectMapper mapper = new ObjectMapper();
            MvcResult mvcResult = mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(sameDtoUser)))
                    .andExpect(status().isOk())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            JsonNode response = new ObjectMapper().readTree(contentAsString);
            assertTrue(response.get("success").asBoolean());
            Optional<User> changedUserOpt = userRepository.findById("login");
            User changedUser = new User();
            if (changedUserOpt.isPresent()) {
                changedUser = changedUserOpt.get();
            }
            assertEquals("ChangedName", changedUser.getName());
            assertEquals("ChangedPassword1", changedUser.getPassword());
            assertEquals(1, userRepository.count());
            List<Role> allUserRoles = roleRepository.findAllByUser(changedUser);
            List<EnumRoles> collectedRoles = allUserRoles.stream().map(role -> role.getName()).collect(Collectors.toList());
            assertTrue(collectedRoles.contains(ADMIN));
            assertTrue(collectedRoles.contains(MANAGER));
            assertTrue(collectedRoles.contains(WORKER));
            assertEquals(3, roleRepository.count());
        }

        @Test
        @SneakyThrows
        @DisplayName("request has empty name -> status 400, return ResponseWithErrors")
        public void wrongNameUserWithRoles() {
            MvcResult mvcResult = mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ClassPathResource("fixtures/userWithEmptyName.json").getInputStream().readAllBytes()))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            JsonNode response = new ObjectMapper().readTree(contentAsString);
            assertFalse(response.get("success").asBoolean());
        }

        @Test
        @SneakyThrows
        @DisplayName("request has password without capital letter -> status 400, return ResponseWithErrors")
        public void wrongPassword1UserWithRoles() {
            DtoUser sameDtoUser = new DtoUser();
            sameDtoUser.setLogin("login");
            sameDtoUser.setName("name");
            sameDtoUser.setPassword("password1");
            sameDtoUser.setRoles(List.of(MANAGER, WORKER, ADMIN));
            ObjectMapper mapper = new ObjectMapper();
            MvcResult mvcResult = mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(sameDtoUser)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            JsonNode response = new ObjectMapper().readTree(contentAsString);
            assertFalse(response.get("success").asBoolean());
        }

        @Test
        @SneakyThrows
        @DisplayName("request has password without numeral -> status 400, return ResponseWithErrors")
        public void wrongPassword2UserWithRoles() {
            DtoUser sameDtoUser = new DtoUser();
            sameDtoUser.setLogin("login");
            sameDtoUser.setName("name");
            sameDtoUser.setPassword("passwordD");
            sameDtoUser.setRoles(List.of(MANAGER, WORKER, ADMIN));
            ObjectMapper mapper = new ObjectMapper();
            MvcResult mvcResult = mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(sameDtoUser)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            JsonNode response = new ObjectMapper().readTree(contentAsString);
            assertFalse(response.get("success").asBoolean());
        }

        @Test
        @SneakyThrows
        @DisplayName("request has  empty list of roles -> status 400, return ResponseWithErrors")
        public void emptyRolesUserWithRoles() {
            DtoUser sameDtoUser = new DtoUser();
            sameDtoUser.setLogin("login");
            sameDtoUser.setName("name");
            sameDtoUser.setPassword("PasswordD1");
            sameDtoUser.setRoles(Collections.EMPTY_LIST);
            ObjectMapper mapper = new ObjectMapper();
            MvcResult mvcResult = mockMvc.perform(put("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(sameDtoUser)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            JsonNode response = new ObjectMapper().readTree(contentAsString);
            assertFalse(response.get("success").asBoolean());
        }
    }

}
