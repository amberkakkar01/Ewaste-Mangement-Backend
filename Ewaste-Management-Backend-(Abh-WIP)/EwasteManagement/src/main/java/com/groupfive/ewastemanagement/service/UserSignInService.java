package com.groupfive.ewastemanagement.service;

import com.groupfive.ewastemanagement.dto.request.PasswordResetDTO;
import com.groupfive.ewastemanagement.dto.request.SignInRequestDTO;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.entity.User;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public interface UserSignInService {

    boolean checkValidUser(SignInRequestDTO signInRequestDTO);

    ResponseEntity<ResponseMessage> resetPassword(PasswordResetDTO passwordResetDTO, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException;

    ResponseEntity<ResponseMessage> signIn(SignInRequestDTO signInRequestDTO, HttpServletResponse response) throws AccountLockedException;

    ResponseEntity<ResponseMessage> signInWithGoogle(String email, HttpServletResponse response);

    void sendResetPasswordMail(User user,String url) throws MessagingException, UnsupportedEncodingException;

}
