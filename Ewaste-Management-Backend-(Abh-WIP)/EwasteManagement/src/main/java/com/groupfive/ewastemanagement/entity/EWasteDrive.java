package com.groupfive.ewastemanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class EWasteDrive {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "collector_id",
            referencedColumnName = "id")
    private User collector;

    @Column(name = "drive_name")
    private String driveName;

    @Column(name = "description")
    private String description;

    @Column(name = "organizer_name")
    private String organizerName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "DRIVE_CATEGORY_MAPPING", joinColumns = {
            @JoinColumn(name = "DRIVE_ID")}, inverseJoinColumns = {
            @JoinColumn(name = "CATEGORY_ID")})
    private Set<CategoriesAccepted> eWasteCategoryAccepted;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "location")
    private String location;

    @Column(name = "city")
    private String city;

    @Column(name = "collector_email")
    private String collectorEmail;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    @CreatedDate
    @Temporal(TemporalType.DATE)
    private Date createdAt;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "updated_at")
    @LastModifiedDate
    @Temporal(TemporalType.DATE)
    private Date updatedAt;

    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

}
