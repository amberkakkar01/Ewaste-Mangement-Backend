package com.groupfive.ewastemanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "requests")
@EntityListeners(AuditingEntityListener.class)
@Data
public class AllPendingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "collector_uid")
    private String collectorUid;

    @Column(name = "status")
    private String status;

    @OneToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "category_id",
            referencedColumnName = "id")
    private CategoriesAccepted category;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "schedule_date")
    private String scheduleDate;

    @Column(name = "schedule_time")
    private String scheduledTime;

    @Column(name = "address")
    private String address;

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
