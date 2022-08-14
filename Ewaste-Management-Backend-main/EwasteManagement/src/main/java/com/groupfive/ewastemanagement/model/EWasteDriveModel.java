package com.groupfive.ewastemanagement.model;

import com.groupfive.ewastemanagement.entity.collectorentity.EWasteDriveCategories;

import javax.validation.constraints.Size;
import java.util.Set;

public class EWasteDriveModel {

    @Size(min = 1,message = "Drive name can't be null")
    private String driveName;

    @Size(min = 1,message = "Description name can't be null")
    private String description;

    @Size(min = 1,message = "Organizer name can't be null")
    private String organizerName;

    private Set<EWasteDriveCategories> eWasteCategoryAccepted;

    @Size(min = 1,message = "Date can't be null")
    private String date;

    @Size(min = 1,message = "Time can't be null")
    private String time;

    private String location;

    private String status;

    public String getDriveName() {
        return driveName;
    }

    public void setDriveName(String driveName) {
        this.driveName = driveName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public Set<EWasteDriveCategories> geteWasteCategoryAccepted() {
        return eWasteCategoryAccepted;
    }

    public void seteWasteCategoryAccepted(Set<EWasteDriveCategories> eWasteCategoryAccepted) {
        this.eWasteCategoryAccepted = eWasteCategoryAccepted;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
