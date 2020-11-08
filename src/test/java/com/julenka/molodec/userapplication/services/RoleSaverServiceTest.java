package com.julenka.molodec.userapplication.services;

import com.julenka.molodec.userapplication.EnumRoles;
import com.julenka.molodec.userapplication.domain.Role;
import com.julenka.molodec.userapplication.domain.User;
import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoleSaverServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RoleSaverService roleSaverService;

    @Test
    public void saveRoles() {
        User user = new User();
        DtoUser dto = new DtoUser();
        dto.setName("name");
        dto.setLogin("login");
        dto.setPassword("wqe");
        dto.setRoles(List.of(EnumRoles.ADMIN));
        Role role = new Role();
        role.setName(dto.getRoles().get(0));
        role.setUser(user);
        roleSaverService.saveRole(dto,user);
        ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleArgumentCaptor.capture());
        Role captured = roleArgumentCaptor.getValue();
        assertEquals(role, captured);
    }

}