package com.groupfive.ewastemanagement.controller;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.userentity.User;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.model.jwtmodel.JwtRequest;
import com.groupfive.ewastemanagement.model.jwtmodel.JwtResponse;
import com.groupfive.ewastemanagement.model.PasswordModel;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.repository.customerrepository.CustomerRepo;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetailsService;
import com.groupfive.ewastemanagement.service.userservice.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.groupfive.ewastemanagement.helper.Constants.*;

@RestController
@Slf4j
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public @ResponseBody ResponseEntity<EnvelopeMessage>registerUser(@Valid @RequestBody UserModel userModel,HttpServletRequest request) throws UsernameNotFoundException {
       return userService.registerUser(userModel,request);
    }

    @PostMapping("/password/save")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel) {
        return userService.savePassword(token,passwordModel);
    }

    @PostMapping("/password/reset")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
      return userService.resetPassword(passwordModel,request);
    }


    //LOGIN CONTROLLER
    @PostMapping("/signin")
    public ResponseEntity<EnvelopeMessage> generateToken(@RequestBody JwtRequest jwtRequest, HttpServletResponse response) throws UsernameNotFoundException {
       return userService.signIn(jwtRequest,response);
    }

    @PostMapping("/signin/google")
    public ResponseEntity<EnvelopeMessage> signInWithGoogle(@RequestParam(name = "email") String email, HttpServletResponse response) throws UsernameNotFoundException {
        return userService.signInWithGoogle(email,response);
    }
}
