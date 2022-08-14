package com.groupfive.ewastemanagement.entity.collectorentity;

import javax.persistence.*;

@Entity
public class CategoriesAccepted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryAccepted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryAccepted() {
        return categoryAccepted;
    }

    public void setCategoryAccepted(String categoryAccepted) {
        this.categoryAccepted = categoryAccepted;
    }
}
