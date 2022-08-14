package com.groupfive.ewastemanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CategoriesAccepted category;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "user_id",
            referencedColumnName = "id")
    private User user;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "scheduled_date")
    private String scheduledDate;

    @Column(name = "scheduled_time")
    private String scheduledTime;

    @Column(name = "collector_uid")
    private String collectorUid;

    @Column(name = "customer_uid")
    private String customerUid;

    @Column(name = "status")
    private String status;

    @Column(name = "order_uid")
    private String orderUid= UUID.randomUUID().toString();

    @Column(name = "item_name")
    private String itemName;

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
