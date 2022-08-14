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
@Data
@EntityListeners(AuditingEntityListener.class)
public class SellItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String collectorUid;
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "collector_id",
            referencedColumnName = "id")
    private User user;

    @Column(name = "item_name")
    private String itemName;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private CategoriesAccepted category;

    @Column(name = "available_quantity")
    private String availableQuantity;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "price")
    private String price;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "vendor_orders_id")
    private VendorOrders vendorOrders;

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
