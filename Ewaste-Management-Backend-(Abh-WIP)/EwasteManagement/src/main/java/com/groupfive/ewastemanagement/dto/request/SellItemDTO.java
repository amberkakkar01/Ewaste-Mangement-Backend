package com.groupfive.ewastemanagement.dto.request;

import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
public class SellItemDTO {

    Long id;

    @NotNull( message = "Item name can't be null")
    String itemName;

    CategoriesAccepted category;

    @NotNull(message = "Quantity can't be null")
    String quantity;

    @NotNull(message = "Price can't be null")
    String price;

    String status;

}