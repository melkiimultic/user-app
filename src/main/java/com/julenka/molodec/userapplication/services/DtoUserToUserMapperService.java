package com.julenka.molodec.userapplication.services;

import com.julenka.molodec.userapplication.EnumRoles;
import com.julenka.molodec.userapplication.domain.Role;
import com.julenka.molodec.userapplication.domain.User;
import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class DtoUserToUserMapperService {

    public User mapDtoUserToUser(DtoUser dtoUser) {
        User user = new User();
        user.setName(dtoUser.getName());
        user.setLogin(dtoUser.getLogin());
        user.setPassword(dtoUser.getPassword());
        user.setRoles(dtoUser.getRoles().stream().map(l -> enumRoleToEntityRole(l, user)).collect(Collectors.toList()));
        return user;
    }

    public Role enumRoleToEntityRole(EnumRoles enumRoles, User user) {
        Role currentRole = new Role();
        currentRole.setName(enumRoles);
        currentRole.setUser(user);
        return currentRole;
    }

}
