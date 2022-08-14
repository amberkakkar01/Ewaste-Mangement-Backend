package com.groupfive.ewastemanagement.entity.collectorentity;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
public class Collector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String uid= UUID.randomUUID().toString();

    @Column(length = 60)
    private String password;

    private String mobileNo;

    private String address1;

    private String city;

    private String state;

    private String pinCode;

    private String gstNo;

    private String registrationNo;

    private String shopTime;

    @OneToMany(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "collector_id",
            referencedColumnName = "id")
    private Set<CategoriesAccepted> categoriesAcceptedSet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public Set<CategoriesAccepted> getCategoriesAcceptedSet() {
        return categoriesAcceptedSet;
    }

    public String getShopTime() {
        return shopTime;
    }

    public void setShopTime(String shopTime) {
        this.shopTime = shopTime;
    }

    public void setCategoriesAcceptedSet(Set<CategoriesAccepted> categoriesAcceptedSet) {
        this.categoriesAcceptedSet = categoriesAcceptedSet;
    }
}
