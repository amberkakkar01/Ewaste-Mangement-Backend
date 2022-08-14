package com.groupfive.ewastemanagement.service;

import com.groupfive.ewastemanagement.dto.request.PasswordResetDTO;
import com.groupfive.ewastemanagement.dto.request.RefreshTokenDTO;
import com.groupfive.ewastemanagement.dto.request.UserDTO;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.entity.User;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    User findUserByEmail(String email);

    ResponseEntity<ResponseMessage> savePassword(String token, PasswordResetDTO passwordResetDTO);

    ResponseEntity<ResponseMessage> registerUser(UserDTO userDTO, HttpServletRequest request);

    ResponseEntity<ResponseMessage> viewProfile(HttpServletRequest request);

    ResponseEntity<ResponseMessage> editProfile(UserDTO userDTO, HttpServletRequest request);

    ResponseEntity<ResponseMessage> refreshToken(RefreshTokenDTO refreshTokenDTO);
}
