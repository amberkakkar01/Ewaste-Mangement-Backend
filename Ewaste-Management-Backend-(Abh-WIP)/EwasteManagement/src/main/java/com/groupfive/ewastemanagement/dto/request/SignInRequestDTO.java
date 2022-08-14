package com.groupfive.ewastemanagement.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class SignInRequestDTO {

    @NotNull
    @Size(min = 11,message = "Email can't be too short!")
    @Email
    String email;

    @Size(min = 6,message = "Password can't be too short!")
    String password;


    public SignInRequestDTO() {
    }

    public SignInRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "JwtRequest{" +
                "userName='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
