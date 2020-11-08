package com.julenka.molodec.userapplication.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name= "USERS")
public class User {
    @Column(name = "Name", nullable = false)
    private String name;


    @Id
    @Column(name = "Login", nullable = false)
    private String login;

    @Column(name = "Password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")

    List<Role> roles = new ArrayList<>();



}
