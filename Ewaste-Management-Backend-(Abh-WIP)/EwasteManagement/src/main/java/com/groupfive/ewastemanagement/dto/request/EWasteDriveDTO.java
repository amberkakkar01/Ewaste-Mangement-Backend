package com.groupfive.ewastemanagement.dto.request;

import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EWasteDriveDTO {

    @NotNull(message = "Drive name can't be null")
    String driveName;

    @NotNull(message = "Description can't be null")
    String description;

    String organizerName;

    Set<CategoriesAccepted> categoryAcceptedSet;

    @NotNull(message = "Date can't be null")
    String date;

    @NotNull(message = "Time can't be null")
    String time;

    String location;

    String status;

}
