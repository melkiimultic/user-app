package com.julenka.molodec.userapplication.services;

import com.julenka.molodec.userapplication.domain.User;
import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserWithRolesSaverService {
    @Autowired
    private DtoUserToUserMapperService dtoUserToUserMapperService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleSaverService roleSaverService;

    public void  saveUserWithRoles (DtoUser dtoUser){
        User user = dtoUserToUserMapperService.mapDtoUserToUser(dtoUser);
        userRepository.save(user);
        roleSaverService.saveRole(dtoUser, user);
    }


}
