package com.groupfive.ewastemanagement.service.implementation;

import com.groupfive.ewastemanagement.dto.request.PasswordResetDTO;
import com.groupfive.ewastemanagement.dto.request.SignInRequestDTO;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.exception.AccountLockedException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.service.UserSignInService;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetailsService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static com.groupfive.ewastemanagement.helper.Constants.*;

@Service
public class UserSignInServiceImplementation implements UserSignInService {

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final JWTUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;

    @Autowired
    public UserSignInServiceImplementation(UserRepo userRepo, AuthenticationManager authenticationManager, JWTUserDetailsService customUserDetailsService, JwtUtil jwtUtil, JavaMailSender mailSender) {
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
    }

    Refill refill = Refill.intervally(3, Duration.ofMinutes(10));
    Bandwidth limit = Bandwidth.classic(3, refill);
    Bucket bucket = Bucket4j.builder()
            .addLimit(limit)
            .build();

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSignInServiceImplementation.class);

    /**
     * This function of service is for validating user by email
     *
     * @param signInRequestDTO JWTRequest parameter
     * @return true if user is valid
     */

    @Override
    public boolean checkValidUser(SignInRequestDTO signInRequestDTO) {
        LOGGER.info("Checking if valid user or not");
        User user = userRepo.findUserByEmail(signInRequestDTO.getEmail());
        LOGGER.info("Checked valid user or not");
        return user != null;
    }



    /**
     * This function of the service extracts the JwtRequest details generates the JWT
     *
     * @param signInRequestDTO for authorization
     * @return Response Entity with status 200 containing the user authorization(JWT)token
     * @throws UsernameNotFoundException if username not found in database
     * @throws BadCredentialsException   if credentials enter doesn't match with credentials
     */

    @Override
    public ResponseEntity<ResponseMessage> signIn(SignInRequestDTO signInRequestDTO, HttpServletResponse response) throws UsernameNotFoundException, BadCredentialsException, AccountLockedException {
        ResponseMessage responseMessage = new ResponseMessage();
        if (bucket.tryConsume(1)) {
            if (checkValidUser(signInRequestDTO)) {
                try {
                    this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequestDTO.getEmail(), signInRequestDTO.getPassword()));
                    org.springframework.security.core.userdetails.UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(signInRequestDTO.getEmail());

                    LOGGER.info("Generating JWT");
                    Map<String, String> token = this.jwtUtil.generateToken(userDetails);
                    LOGGER.info("JWT generated");

                    response.setHeader(AUTHORIZATION, BEARER + " " + token.get("Access-Token"));
                    response.setHeader(EMAIL, signInRequestDTO.getEmail());
                    response.setHeader(TRACE_ID, UUID.randomUUID().toString());

                    responseMessage.setStatus(SUCCESS);
                    responseMessage.setData(token);

                    LOGGER.info("Returning user details and JWT");

                    return ResponseEntity.ok(responseMessage);
                } catch (UsernameNotFoundException | BadCredentialsException exception) {
                    responseMessage.setStatus(FAIL);
                    responseMessage.setData("Bad Credentials");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);

                }
            } else {
                responseMessage.setStatus(FAIL);
                responseMessage.setData(NO_USER_FOUND);
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        } else {
            throw new com.groupfive.ewastemanagement.exception.AccountLockedException("Account Locked for 10 Minutes :: Please try again later");
        }
    }

    /**
     * This function of the service extracts the JwtRequest details generates the JWT
     *
     * @param email String parameter
     * @return Response Entity with status 200 containing the user authorization(JWT)token
     * @throws UsernameNotFoundException if username not found in database
     * @throws BadCredentialsException   if credentials enter doesn't match with credentials
     */

    @Override
    public ResponseEntity<ResponseMessage> signInWithGoogle(String email, HttpServletResponse response) {
        LOGGER.info("User with id '{}' is signIn with google", email);
        User user = userRepo.findUserByEmail(email);
        ResponseMessage responseMessage = new ResponseMessage();
        if (user != null) {
            org.springframework.security.core.userdetails.UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(email);

            LOGGER.info("Generating JWT for user with id '{}'", user.getEmail());

            Map<String, String> token = this.jwtUtil.generateToken(userDetails);

            LOGGER.info("JWT Token generated for user with id '{}'", user.getEmail());

            response.setHeader(AUTHORIZATION, BEARER + " " + token.get("Access-Token"));
            response.setHeader(EMAIL, email);

            LOGGER.info("User with id '{}' has signIn successfully", user.getEmail());
            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(token);

            return ResponseEntity.ok(responseMessage);
        } else {
            responseMessage.setStatus(FAIL);
            responseMessage.setData(NO_USER_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * This function of service is for reset password
     *
     * @param passwordResetDTO which contains email
     * @param request       HTTPServletRequest
     * @return passwordToken Mail with a unique link
     */

    @Override
    public ResponseEntity<ResponseMessage> resetPassword(PasswordResetDTO passwordResetDTO, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        User user = userRepo.findUserByEmail(passwordResetDTO.getEmail());
        String url;
        if (user != null) {
            org.springframework.security.core.userdetails.UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(passwordResetDTO.getEmail());
            String token = this.jwtUtil.generatePasswordResetToken(userDetails);
            url = passwordResetTokenMail(user, token);
        } else {
            throw new InvalidUserException("User not found in records");
        }
        return new ResponseEntity<>(new ResponseMessage(SUCCESS, url), HttpStatus.CREATED);
    }

    /**
     * This function of the service sends password reset mail to users
     *
     * @param user    user
     * @param siteURL String Parameter
     */

    @Override
    public void sendResetPasswordMail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "ewastemanagementindia@gmail.com";
        String senderName = E_WASTE_MANAGEMENT;
        String content = "Dear [[name]],<br>"
                + "Please click the link below to reset your Password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">Reset Password</a></h3>"
                + "Thank you,<br>"
                + senderName;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(RESET_PASSWORD);

        content = content.replace("[[name]]", user.getFirstName());

        content = content.replace("[[URL]]", siteURL);

        helper.setText(content, true);

        LOGGER.info("Password Reset Mail sent to user with id '{}'", user.getEmail());

        mailSender.send(message);
    }

    String passwordResetTokenMail(User user, String token) throws MessagingException, UnsupportedEncodingException {
        String url =
                URL_EMAIL_RESET_PASSWORD
                        + token;
        sendResetPasswordMail(user, url);
        return url;
    }

}
