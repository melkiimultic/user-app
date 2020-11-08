package com.julenka.molodec.userapplication.services;

import com.julenka.molodec.userapplication.dtoObjects.UserWithoutRoles;
import com.julenka.molodec.userapplication.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserToUserWithoutRolesMapperService {
    public UserWithoutRoles mapUserToUserWithoutRoles(User user){
        UserWithoutRoles userWithoutRoles = new UserWithoutRoles();
        userWithoutRoles.setLogin(user.getLogin());
        userWithoutRoles.setName(user.getName());
        userWithoutRoles.setPassword(user.getPassword());
        return userWithoutRoles;
    }
}
