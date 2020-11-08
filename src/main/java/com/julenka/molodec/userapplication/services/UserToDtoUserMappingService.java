package com.julenka.molodec.userapplication.services;
import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.EnumRoles;
import com.julenka.molodec.userapplication.domain.Role;
import com.julenka.molodec.userapplication.domain.User;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserToDtoUserMappingService {

    @Transactional()
    public DtoUser mapUserToDtoUser(User user) {

        DtoUser dtoUser = new DtoUser();
        dtoUser.setName(user.getName());
        dtoUser.setLogin(user.getLogin());
        dtoUser.setPassword(user.getPassword());
        List<EnumRoles> userRoles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            userRoles.add(role.getName());
        }
        dtoUser.setRoles(userRoles);
        return dtoUser;

    }
}
