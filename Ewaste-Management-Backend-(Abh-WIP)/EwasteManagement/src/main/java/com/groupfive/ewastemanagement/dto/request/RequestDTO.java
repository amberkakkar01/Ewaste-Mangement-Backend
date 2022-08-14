package com.groupfive.ewastemanagement.dto.request;

import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class RequestDTO {

    private CategoriesAccepted category;

    @NotNull(message = "Quantity can't be null")
    String quantity;

    @NotNull(message = "Request Type can't be null")
    String requestType;

    @NotNull(message = "Scheduled date can't be null")
    String scheduledDate;

    @NotNull(message = "Time can't be null")
    String scheduledTime;

    String collectorUid;

    @NotNull(message = "Item can't be null")
    String itemName;
}
