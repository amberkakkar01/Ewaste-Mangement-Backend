package com.groupfive.ewastemanagement.controller;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.PasswordResetDTO;
import com.groupfive.ewastemanagement.dto.request.RefreshTokenDTO;
import com.groupfive.ewastemanagement.dto.request.UserDTO;
import com.groupfive.ewastemanagement.dto.request.SignInRequestDTO;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.service.UserService;
import com.groupfive.ewastemanagement.service.UserSignInService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

import static com.groupfive.ewastemanagement.helper.Constants.AUTHORIZATION;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserSignInService userSignInService;


    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil,UserSignInService userSignInService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userSignInService=userSignInService;
    }

    /**
     * This controller is for handling the API calls to register new user
     *
     * @param userDTO which contains fields (first-Name, last-Name, email, password, pin-code)
     * @param request   HTTPServletRequest
     * @return Response Entity with status code 201 and displays details of new user in the body
     */

    @PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to register new user")
    public @ResponseBody ResponseEntity<ResponseMessage> registerUser(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) throws UsernameNotFoundException {
        LOGGER.debug("User with id '{}' is trying to sign-up", userDTO.getEmail());
        return userService.registerUser(userDTO, request);
    }

    /**
     * This controller is for handling the API calls to save new password
     *
     * @param token         String Parameter to verify user
     * @param passwordResetDTO which contains Email and new-Password
     * @return Response Entity with status code 200 and displays success message
     */

    @PostMapping(value = "/password/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to save new password")
    public ResponseEntity<ResponseMessage> savePassword(@RequestParam("token") String token,
                                                        @RequestBody PasswordResetDTO passwordResetDTO) {
        LOGGER.debug("User with id '{}' is entering new password", passwordResetDTO.getEmail());
        return userService.savePassword(token, passwordResetDTO);
    }

    /**
     * This controller is for handling the API calls to reset password
     *
     * @param request       HTTPServletRequest
     * @param passwordResetDTO which contains Email and new-Password
     * @return Response Entity with status code 200 and displays success message
     */

    @PostMapping(value = "/password/reset", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "This controller is for handling the API calls to reset password")
    public ResponseEntity<ResponseMessage> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        LOGGER.debug("User with id '{}' is performing password reset", passwordResetDTO.getEmail());
        return userSignInService.resetPassword(passwordResetDTO, request);
    }

    /**
     * This controller is for handling the API calls to view profile details
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays profile details
     */

    @PreAuthorize("hasAnyAuthority('VENDOR','COLLECTOR','CUSTOMER')")
    @GetMapping(value = "/profile/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view profile details")
    public ResponseEntity<ResponseMessage> viewProfile(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching his profile details", id);
        return userService.viewProfile(request);
    }

    /**
     * This controller is for handling the API calls to modify profile details
     *
     * @param userDTO which contains fields (first-Name, last-Name, email, password, pin-code)
     * @param request   HTTPServletRequest
     * @return Response Entity with status code 200 and displays modified profile details
     */

    @PreAuthorize("hasAnyAuthority('VENDOR','COLLECTOR','CUSTOMER')")
    @PutMapping(value = "/profile/edit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to modify profile details")
    public ResponseEntity<ResponseMessage> editProfile(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' modifying his profile details", id);
        return userService.editProfile(userDTO, request);
    }

    /**
     * This controller is for handling the API calls sign-In
     *
     * @param signInRequestDTO JWT-Request
     * @return Response Entity with status code 200 and displays success message with JWT Token
     */

    @PostMapping(value = "/sign-in", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls sign-In")
    public ResponseEntity<ResponseMessage> signIn(@Valid @RequestBody SignInRequestDTO signInRequestDTO, HttpServletResponse response) throws UsernameNotFoundException, AccountLockedException {
        LOGGER.debug("User with id '{}' is trying to sign-in", signInRequestDTO.getEmail());
        return userSignInService.signIn(signInRequestDTO, response);
    }

    /**
     * This controller is for handling the API calls sign-In with Google
     *
     * @param email String parameter
     * @return Response Entity with status code 200 and displays success message with JWT Token
     */

    @PostMapping(value = "/sign-in/google", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls sign-In with google")
    public ResponseEntity<ResponseMessage> signInWithGoogle(@RequestParam(name = "email") String email, HttpServletResponse response) throws UsernameNotFoundException {
        LOGGER.debug("User with id '{}' is trying to sign-in with google", email);
        return userSignInService.signInWithGoogle(email, response);
    }

    /**
     * This controller is for handling the API calls to refresh Access Token
     *
     * @param refreshTokenDTO which contains Access Token
     * @return Response Entity with status code 200 and displays success message with new Access and Refresh Token
     */

    @PostMapping(value = "/refresh/token", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseMessage> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        return userService.refreshToken(refreshTokenDTO);
    }
}
