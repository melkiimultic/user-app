package com.julenka.molodec.userapplication.services;

import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.EnumRoles;
import com.julenka.molodec.userapplication.domain.Role;
import com.julenka.molodec.userapplication.domain.User;
import com.julenka.molodec.userapplication.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleSaverService {
    @Autowired
    private RoleRepository roleRepository;

    public void saveRole (DtoUser dtoUser, User user){

        for (EnumRoles enumRoles : dtoUser.getRoles()) {
            Role role = new Role();
            role.setName(enumRoles);
            role.setUser(user);
          roleRepository.save(role);
        }

    }
}
