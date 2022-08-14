package com.groupfive.ewastemanagement.dto.request;

import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.Role;
import lombok.Data;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class UserDTO {

    @NotNull
    @Size(min = 2,message = "First Name Compulsory")
    String firstName;

    @NotNull
    @Size(min = 2,message = "Last Name Compulsory")
    String lastName;

    @NotNull
    @Email
    @Size(min = 9,message = "Email is Compulsory")
    String email;

    Role role;

    @NotNull
    @Size(min = 10,message = "Enter a valid mobile no")
    String mobileNo;

    @NotNull
    @Size(min = 2,message = "Password is Compulsory")
    String password;

    String matchingPassword;

    @NotNull
    @Size(min = 1, message = "Address can't be null" )
    String address1;

    String gstNo;

    String registrationNo;


    @NotNull
    @Size(min = 1, message = "City can't be null" )
    String city;

    @NotNull
    @Size(min = 1, message = "State can't be null" )
    String state;

    @NotNull
    @Size(min = 1, message = "PinCode can't be null" )
    String pinCode;

    String shopTime;

    Set<CategoriesAccepted> categoriesAcceptedSet;

}