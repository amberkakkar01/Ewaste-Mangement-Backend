package com.groupfive.ewastemanagement.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Data
public class CategoriesAccepted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category")
    private String categoryAccepted;


}
