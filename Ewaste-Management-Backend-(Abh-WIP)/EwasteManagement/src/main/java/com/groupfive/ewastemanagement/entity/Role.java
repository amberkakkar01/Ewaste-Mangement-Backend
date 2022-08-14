package com.groupfive.ewastemanagement.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

}
