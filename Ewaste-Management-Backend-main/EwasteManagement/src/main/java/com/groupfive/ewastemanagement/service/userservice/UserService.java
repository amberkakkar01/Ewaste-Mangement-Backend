package com.groupfive.ewastemanagement.service.userservice;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.userentity.User;
import com.groupfive.ewastemanagement.model.PasswordModel;
import com.groupfive.ewastemanagement.model.jwtmodel.JwtRequest;
import com.groupfive.ewastemanagement.model.UserModel;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface UserService {
    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    ResponseEntity<EnvelopeMessage> registerCustomer(UserModel userModel);

    ResponseEntity<EnvelopeMessage> registerVendor(UserModel userModel);

    ResponseEntity<EnvelopeMessage> registerCollector(UserModel userModel);

    boolean checkValidUser(JwtRequest jwtRequest);

    String savePassword(String token, PasswordModel passwordModel);

    String resetPassword(PasswordModel passwordModel, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException;

    ResponseEntity<EnvelopeMessage> signIn(JwtRequest jwtRequest, HttpServletResponse response);

    ResponseEntity<EnvelopeMessage> registerUser(UserModel userModel, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> signInWithGoogle(String email, HttpServletResponse response);
}
