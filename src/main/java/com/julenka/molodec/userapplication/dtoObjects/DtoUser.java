package com.julenka.molodec.userapplication.dtoObjects;

import com.julenka.molodec.userapplication.EnumRoles;
import lombok.Data;

import java.util.List;

@Data
public class DtoUser {

    private String name;

    private String login;

    private String password;

    List<EnumRoles> roles;
}
