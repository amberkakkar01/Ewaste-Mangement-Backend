package com.groupfive.ewastemanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gst_no")
    private String gstNo;

    @Column(name = "registration_no")
    private String registrationNo;

    @Column(name = "shop_time")
    private String shopTime;

    @Column(name = "uid")
    private String uid;

    @OneToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "user_id",
            referencedColumnName = "id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.ALL,
            })
    @JoinTable(name = "USER_CATEGORIES_MAPPING", joinColumns = {
            @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "CATEGORY_ID") })
    private Set<CategoriesAccepted> categoriesAccepted=new HashSet<>();

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
