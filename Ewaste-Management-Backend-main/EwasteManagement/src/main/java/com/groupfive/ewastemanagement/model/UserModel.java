package com.groupfive.ewastemanagement.model;

import com.groupfive.ewastemanagement.entity.collectorentity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.userentity.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;


public class UserModel {

    @NotNull
    @Size(min = 2,message = "First Name Compulsory")
    private String firstName;

    @NotNull
    @Size(min = 2,message = "Last Name Compulsory")
    private String lastName;

    @NotNull
    @Email
    @Size(min = 9,message = "Email is Compulsory")
    private String email;

    //role
    private Role role;

    @NotNull
    @Size(min = 10,message = "Enter a vaild mobile no")
    private String mobileNo;

    @NotNull
    @Size(min = 2,message = "Password is Compulsory")
    private String password;

    private String matchingPassword;

    @NotNull
    @Size(min = 1, message = "Address can't be null" )
    private String address1;

    private String gstNo;

    private String registrationNo;

    @NotNull
    @Size(min = 1, message = "City can't be null" )
    private String city;

    @NotNull
    @Size(min = 1, message = "State can't be null" )
    private String state;

    @NotNull
    @Size(min = 1, message = "Pincode can't be null" )
    private String pinCode;

    private String shopTime;

    private Set<CategoriesAccepted> categoriesAcceptedSet;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
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