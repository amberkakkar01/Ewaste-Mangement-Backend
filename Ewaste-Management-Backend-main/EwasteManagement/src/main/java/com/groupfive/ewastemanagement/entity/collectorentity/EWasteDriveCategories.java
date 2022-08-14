package com.groupfive.ewastemanagement.entity.collectorentity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class EWasteDriveCategories {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
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
