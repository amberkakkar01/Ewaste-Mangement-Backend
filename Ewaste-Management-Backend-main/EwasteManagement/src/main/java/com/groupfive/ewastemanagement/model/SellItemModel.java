package com.groupfive.ewastemanagement.model;

import javax.validation.constraints.Size;

public class SellItemModel {

    private Long id;

    @Size(min = 1, message = "Item name can't be null")
    private String itemName;

    @Size(min = 1, message = "Category name can't be null")
    private String category;

    @Size(min = 1, message = "Quantity name can't be null")
    private String quantity;

    @Size(min = 1, message = "Price name can't be null")
    private String price;

    private String status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}