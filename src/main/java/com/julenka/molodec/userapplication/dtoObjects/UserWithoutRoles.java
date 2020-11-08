package com.julenka.molodec.userapplication.dtoObjects;

import lombok.Data;

@Data
public class UserWithoutRoles {
    private String name;
    private String login;
    private String password;
}
