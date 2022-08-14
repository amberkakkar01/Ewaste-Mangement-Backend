package com.groupfive.ewastemanagement.entity.collectorentity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class EWasteDrive {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String driveName;
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "collector_id",
            referencedColumnName = "id")
    private Collector collector;

    private String description;

    private String organizerName;

    @OneToMany(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "collector_id",
            referencedColumnName = "id")
    private Set<EWasteDriveCategories> eWasteCategoryAccepted;

    private String date;

    private String time;

    private String location;

    private String city;

    private String collectorEmail;

    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCollectorEmail() {
        return collectorEmail;
    }

    public void setCollectorEmail(String collectorEmail) {
        this.collectorEmail = collectorEmail;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }
}
