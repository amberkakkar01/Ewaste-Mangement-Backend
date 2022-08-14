package com.groupfive.ewastemanagement.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AcceptItemOnSaleDTO {

    @NotNull(message = "Enter a valid Order Id")
    Long id;

    @NotNull(message = "Quantity can't be null")
    String quantity;

    String price;

    @NotEmpty(message = "Date can't be null")
    String date;
}
