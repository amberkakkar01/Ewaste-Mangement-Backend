package com.groupfive.ewastemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.PasswordResetToken;
import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.customerentity.Customer;
import com.groupfive.ewastemanagement.entity.userentity.Role;
import com.groupfive.ewastemanagement.entity.userentity.User;
import com.groupfive.ewastemanagement.entity.vendorentity.Vendor;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.model.PasswordModel;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.model.jwtmodel.JwtRequest;
import com.groupfive.ewastemanagement.model.jwtmodel.JwtResponse;
import com.groupfive.ewastemanagement.repository.PasswordResetTokenRepository;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.CustomerRepo;
import com.groupfive.ewastemanagement.repository.userrepository.UserRepo;
import com.groupfive.ewastemanagement.repository.vendorrepository.VendorRepo;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetails;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetailsService;
import com.groupfive.ewastemanagement.service.userservice.UserService;
import com.groupfive.ewastemanagement.service.userservice.UserServiceImplementation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private UserController userService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private CustomerRepo customerRepo;

    @MockBean
    private VendorRepo vendorRepo;

    @MockBean
    private CollectorRepo collectorRepo;

    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    JWTUserDetailsService userDetailsService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    AuthenticationManager authenticationManager;

    @Autowired
    private JavaMailSender javaMailSender;

    User user;

    UserModel userModel ;
    Customer customer;

    Collector collector;

    Vendor vendor;

    @BeforeAll
    public void setUp()
    {
        userModel= new UserModel();
        userModel.setEmail("abcd@gmail.com");
        userModel.setFirstName("abs");
        userModel.setLastName("lk");
        userModel.setMobileNo("9898989898");

        Role role = new Role();
        role.setName("CUSTOMER");

        userModel.setRole(role);
        userModel.setCity("london");
        userModel.setState("Uk");
        userModel.setPassword("123456");
        userModel.setMatchingPassword("123456");
        userModel.setAddress1("Hno-1 Street");

        user=new User();
        user.setFirstName("Alex");
        user.setLastName("Singh");
        user.setEmail("abcd@gmail.com");
        user.setMobileNo("9050505050");
        user.setPassword("123456");

        customer = new Customer();
        customer.setEmail("abcd@gmail.com");
        customer.setFirstName("abs");
        customer.setLastName("lk");
        customer.setMobileNo("9898989898");
        customer.setCity("london");
        customer.setState("Uk");
        customer.setPassword("123456");
        customer.setAddress1("Hno-1 Street");

        collector = new Collector();
        collector.setEmail("coll@gmail.com");
        collector.setFirstName("abs");
        collector.setLastName("lk");
        collector.setMobileNo("9898989898");
        collector.setCity("london");
        collector.setState("Uk");
        collector.setPassword("123456");
        collector.setAddress1("Hno-1 Street");

        vendor = new Vendor();
        vendor.setEmail("coll@gmail.com");
        vendor.setFirstName("abs");
        vendor.setLastName("lk");
        vendor.setMobileNo("9898989898");
        vendor.setCity("london");
        vendor.setState("Uk");
        vendor.setPassword("123456");
        vendor.setAddress1("Hno-1 Street");
    }


    @Test
    void registerUser_Customer()
    {
        Role role=new Role();
        role.setName(Constants.CUSTOMER);
        userModel.setRole(role);
        MockHttpServletRequest request = new MockHttpServletRequest();
        userService.registerUser(userModel,request);
    }

    @Test
    void registerUser_Collector()
    {
        Role role=new Role();
        role.setName(Constants.COLLECTOR);
        userModel.setRole(role);
        MockHttpServletRequest request = new MockHttpServletRequest();
        userService.registerUser(userModel,request);
    }

    @Test
    void registerUser_Vendor()
    {
        Role role=new Role();
        role.setName(Constants.VENDOR);
        userModel.setRole(role);
        MockHttpServletRequest request = new MockHttpServletRequest();
        userService.registerUser(userModel,request);
    }


    @Test
    void signIn_NoUserFound()
    {
        JwtRequest jwtRequest=new JwtRequest();
        jwtRequest.setEmail("user@gmail.com");
        jwtRequest.setPassword("123456");
        MockHttpServletResponse response = new MockHttpServletResponse();

        EnvelopeMessage envelopeMessage=userService.generateToken(jwtRequest,response).getBody();
        assertEquals("No user Found",envelopeMessage.getData());
    }

    @Test
    void signIn()
    {
        JwtRequest jwtRequest=new JwtRequest();
        jwtRequest.setEmail(user.getEmail());
        jwtRequest.setPassword(user.getPassword());

        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(true);

        MockHttpServletResponse response=new MockHttpServletResponse();

        JWTUserDetails userDetails;

        userDetails=new JWTUserDetails(user);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);


        when(jwtUtil.generateToken(userDetails)).thenReturn("123456");
        when(userDetailsService.loadUserByUsername(eq(user.getEmail()))).thenReturn(userDetails);
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);

        EnvelopeMessage envelopeMessage=userService.generateToken(jwtRequest,response).getBody();
        JwtResponse jwtResponse= (JwtResponse) envelopeMessage.getData();
        assertEquals("123456",jwtResponse.getToken());
    }

    @Test
    void savePassword_InvalidToken()
    {
        PasswordModel passwordModel = new PasswordModel();
        passwordModel.setEmail(user.getEmail());
        String token="123456";
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(null);
        assertEquals(Constants.INVALID_TOKEN,userService.savePassword(token,passwordModel));
    }

    @Test
    void savePassword_ValidToken()
    {
        String token="test";
        PasswordResetToken passwordResetToken=new PasswordResetToken();
        passwordResetToken.setToken(token);

        PasswordModel passwordModel = new PasswordModel();
        passwordModel.setEmail(user.getEmail());

        passwordResetToken.setExpirationTime(Calendar.getInstance().getTime());
        passwordResetToken.setUser(user);
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);

        assertEquals(Constants.INVALID_TOKEN,userService.savePassword(token,passwordModel));
    }

    @Test
    void savePassword_PasswordResetSuccessful()
    {
        String token="test";
        PasswordResetToken passwordResetToken=new PasswordResetToken();
        passwordResetToken.setToken(token);

        PasswordModel passwordModel = new PasswordModel();
        passwordModel.setEmail(user.getEmail());

        passwordResetToken.setUser(user);

        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, 15);
        passwordResetToken.setExpirationTime(c.getTime());
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);


        Set<Role> roles=new HashSet<>();
        Role role=new Role();
        role.setName(Constants.CUSTOMER);
        roles.add(role);

        user.setRoles(roles);
        when(customerRepo.findByEmail(user.getEmail())).thenReturn(customer);
        when(userRepo.save(user)).thenReturn(user);
        when(customerRepo.save(customer)).thenReturn(customer);

        assertEquals(Constants.PASSWORD_RESET_SUCCESSFULLY,userService.savePassword(token,passwordModel));
    }

    @Test
    void resetPassword() throws MessagingException, UnsupportedEncodingException {
        MockHttpServletRequest request=new MockHttpServletRequest();
        PasswordModel passwordModel=new PasswordModel();
        passwordModel.setEmail(user.getEmail());

        when(userRepo.findByEmail(passwordModel.getEmail())).thenReturn(user);
        String token="1234";

        PasswordResetToken passwordResetToken=new PasswordResetToken(user,token);
        when(passwordResetTokenRepository.save(passwordResetToken)).thenReturn(passwordResetToken);

        String url="http://localhost:3000/password/save/";

        assertEquals(url,userService.resetPassword(passwordModel,request).substring(0,36));
    }
}