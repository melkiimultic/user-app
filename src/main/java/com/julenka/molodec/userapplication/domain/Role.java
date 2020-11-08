package com.julenka.molodec.userapplication.domain;

import com.julenka.molodec.userapplication.EnumRoles;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name= "ROLES")
public class Role {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "Name", nullable = false)
    private EnumRoles name;

    @JoinColumn(name= "UserLogin")
    @ManyToOne()
    private User user;


}
