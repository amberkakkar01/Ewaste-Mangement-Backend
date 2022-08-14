package com.groupfive.ewastemanagement.model;

import javax.validation.constraints.Size;

public class RequestModel {

    @Size(min = 1,message = "Category can't be null")
    private String category;

    @Size(min = 1,message = "Quantity can't be null")
    private String quantity;

    private String requestType;

    private String scheduledDate;

    private String scheduledTime;

    private String collectorUid;

    @Size(min = 1,message = "Item can't be null")
    private String itemName;

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

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
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
}
