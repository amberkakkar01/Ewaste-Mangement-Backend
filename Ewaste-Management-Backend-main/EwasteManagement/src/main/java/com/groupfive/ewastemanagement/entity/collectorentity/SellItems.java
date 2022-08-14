package com.groupfive.ewastemanagement.entity.collectorentity;

import javax.persistence.*;

@Entity
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
    private Collector collector;

    private String itemName;

    private String category;

    private String availableQuantity;

    private String quantity;

    private String price;

    private String status;

    private String vendorUid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCollectorUid() {
        return collectorUid;
    }

    public void setCollectorUid(String collectorUid) {
        this.collectorUid = collectorUid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVendorUid() {
        return vendorUid;
    }

    public void setVendorUid(String vendorUid) {
        this.vendorUid = vendorUid;
    }

    public String getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(String availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }
}
