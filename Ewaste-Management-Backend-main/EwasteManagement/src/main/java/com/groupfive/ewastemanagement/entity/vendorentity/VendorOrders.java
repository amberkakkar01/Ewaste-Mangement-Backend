package com.groupfive.ewastemanagement.entity.vendorentity;

import com.groupfive.ewastemanagement.entity.collectorentity.Collector;

import javax.persistence.*;

@Entity
public class VendorOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String collectorUid;

    private String itemName;

    private String category;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "vendor_id",
            referencedColumnName = "id")
    private Vendor vendor;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "collector_id",
            referencedColumnName = "id")
    private Collector collector;

    private String quantity;

    private String price;

    private String address;

    private String date;

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

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
