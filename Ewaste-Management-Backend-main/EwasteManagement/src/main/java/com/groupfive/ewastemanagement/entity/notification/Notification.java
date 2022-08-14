package com.groupfive.ewastemanagement.entity.notification;

import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.customerentity.Customer;

import javax.persistence.*;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    private String customerUid;

    private String collectorUid;

    private String message;

    private boolean status;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "customer_id",
            referencedColumnName = "id")
    private Customer customer;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "collector_id",
            referencedColumnName = "id")
    private Collector collector;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public void setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
    }

    public String getCollectorUid() {
        return collectorUid;
    }

    public void setCollectorUid(String collectorUid) {
        this.collectorUid = collectorUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
