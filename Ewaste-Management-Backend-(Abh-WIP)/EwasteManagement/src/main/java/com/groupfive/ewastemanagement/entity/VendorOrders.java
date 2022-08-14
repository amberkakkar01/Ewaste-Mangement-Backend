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
public class VendorOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "sales_id",
            referencedColumnName = "id")
    private SellItems sellItems;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "price")
    private String price;

    @Column(name = "address")
    private String address;

    @Column(name = "date")
    private String date;

    @Column(name = "status")
    private String status;

    @Column(name = "vendor_uid")
    private String vendorUid;

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
