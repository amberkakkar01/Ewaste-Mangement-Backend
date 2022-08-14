package com.groupfive.ewastemanagement.service.userservice;

import com.groupfive.ewastemanagement.config.JwtAuthFilter;
import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.customerentity.Customer;
import com.groupfive.ewastemanagement.entity.PasswordResetToken;
import com.groupfive.ewastemanagement.entity.userentity.Role;
import com.groupfive.ewastemanagement.entity.userentity.User;
import com.groupfive.ewastemanagement.entity.vendorentity.Vendor;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.model.PasswordModel;
import com.groupfive.ewastemanagement.model.jwtmodel.JwtRequest;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.model.jwtmodel.JwtResponse;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.CustomerRepo;
import com.groupfive.ewastemanagement.repository.PasswordResetTokenRepository;
import com.groupfive.ewastemanagement.repository.userrepository.UserRepo;
import com.groupfive.ewastemanagement.repository.vendorrepository.VendorRepo;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetails;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetailsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.groupfive.ewastemanagement.helper.Constants.URL_EMAIL_RESETPASSWORD;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserServiceImplementation userServiceImplementation;

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
    void findUserByEmail() {
        String email = "abcd@gmail.com";

        when(userRepo.findByEmail(email)).thenReturn(user);
        User user1 = userService.findUserByEmail(user.getEmail());

        assertEquals(user.getEmail(),user1.getEmail());
    }

    @Test
    void createPasswordResetTokenForUser() {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken("213434");
        passwordResetToken.setUser(user);

        when(passwordResetTokenRepository.save(passwordResetToken)).thenReturn(passwordResetToken);
        userService.createPasswordResetTokenForUser(user,passwordResetToken.getToken());

        assertEquals(user,passwordResetToken.getUser());
    }

    @Test
    void changePassword_Customer() {
        Set<Role>roles=new HashSet<>();
        Role role=new Role();
        role.setName(Constants.CUSTOMER);
        roles.add(role);

        user.setRoles(roles);
        when(customerRepo.findByEmail(user.getEmail())).thenReturn(customer);
        when(userRepo.save(user)).thenReturn(user);
        when(customerRepo.save(customer)).thenReturn(customer);

        userService.changePassword(user,"1234567");

        assertEquals(userModel.getEmail(),user.getEmail());
    }

    @Test
    void changePassword_Collector() {
        Set<Role>roles=new HashSet<>();
        Role role=new Role();
        role.setName(Constants.COLLECTOR);
        roles.add(role);

        user.setRoles(roles);
        when(collectorRepo.findByEmail(user.getEmail())).thenReturn(collector);
        when(userRepo.save(user)).thenReturn(user);
        when(collectorRepo.save(collector)).thenReturn(collector);

        userService.changePassword(user,"1234567");

        assertEquals(userModel.getEmail(),user.getEmail());
    }

    @Test
    void changePassword_Vendor() {
        Set<Role>roles=new HashSet<>();
        Role role=new Role();
        role.setName(Constants.VENDOR);
        roles.add(role);

        user.setRoles(roles);
        when(vendorRepo.findByEmail(user.getEmail())).thenReturn(vendor);
        when(userRepo.save(user)).thenReturn(user);
        when(vendorRepo.save(vendor)).thenReturn(vendor);

        userService.changePassword(user,"1234567");

        assertEquals(userModel.getEmail(),user.getEmail());
    }

    @Test
    void registerCustomer() {

        when(customerRepo.findByEmail(userModel.getEmail())).thenReturn(null);
        when(userRepo.findByEmail(userModel.getEmail())).thenReturn(null);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) userService.registerCustomer(userModel).getBody();
        Customer c=(Customer)envelopeMessage.getData();
        assertEquals(userModel.getEmail(), c.getEmail());

        Customer customer = new Customer();
        when(customerRepo.findByEmail(userModel.getEmail())).thenReturn(customer);
        when(userRepo.findByEmail(userModel.getEmail())).thenReturn(null);

        EnvelopeMessage envelopeMessage1 = (EnvelopeMessage) userService.registerCustomer(userModel).getBody();

        assertEquals("Customer already registered with same e-mail Id", envelopeMessage1.getData());

        User user = new User();
        when(customerRepo.findByEmail(userModel.getEmail())).thenReturn(null);
        when(userRepo.findByEmail(userModel.getEmail())).thenReturn(user);

        EnvelopeMessage envelopeMessage2 = (EnvelopeMessage) userService.registerCustomer(userModel).getBody();

        assertEquals("Customer already registered with same e-mail Id in Another Profile", envelopeMessage2.getData());
    }

    @Test
    void registerVendor() {
        Role role = new Role();
        role.setName("VENDOR");
        userModel.setRole(role);

        when(vendorRepo.findByEmail(userModel.getEmail())).thenReturn(null);
        when(userRepo.findByEmail(userModel.getEmail())).thenReturn(null);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) userService.registerVendor(userModel).getBody();
        Vendor c=(Vendor) envelopeMessage.getData();

        assertEquals(userModel.getEmail(), c.getEmail());

        Vendor vendor = new Vendor();
        User user = new User();

        when(vendorRepo.findByEmail(userModel.getEmail())).thenReturn(vendor);
        when(userRepo.findByEmail(userModel.getEmail())).thenReturn(null);
        EnvelopeMessage envelopeMessage1 = (EnvelopeMessage) userService.registerVendor(userModel).getBody();

        assertEquals("Vendor already registered with same e-mail Id",envelopeMessage1.getData());

        when(vendorRepo.findByEmail(userModel.getEmail())).thenReturn(null);
        when(userRepo.findByEmail(userModel.getEmail())).thenReturn(user);
        EnvelopeMessage envelopeMessage2 = (EnvelopeMessage) userService.registerVendor(userModel).getBody();

        assertEquals("Vendor already registered with same e-mail Id in Another Profile",envelopeMessage2.getData());
    }

    @Test
    void registerCollector() {

        Role role = new Role();
        role.setName("COLLECTOR");

        userModel.setRole(role);

        when(collectorRepo.findByEmail(userModel.getEmail())).thenReturn(null);
        when(userRepo.findByEmail(userModel.getEmail())).thenReturn(null);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) userService.registerCollector(userModel).getBody();
        Collector c=(Collector) envelopeMessage.getData();

        assertEquals(userModel.getEmail(), c.getEmail());

        Collector collector  = new Collector();
        User user = new User();

        when(collectorRepo.findByEmail(userModel.getEmail())).thenReturn(collector);
        when(userRepo.findByEmail(userModel.getEmail())).thenReturn(null);

        EnvelopeMessage envelopeMessage1 = (EnvelopeMessage) userService.registerCollector(userModel).getBody();
        assertEquals("Collector already registered with same e-mail Id",envelopeMessage1.getData());

        when(collectorRepo.findByEmail(userModel.getEmail())).thenReturn(null);
        when(userRepo.findByEmail(userModel.getEmail())).thenReturn(user);

        EnvelopeMessage envelopeMessage2 = (EnvelopeMessage) userService.registerCollector(userModel).getBody();
        assertEquals("Collector already registered with same e-mail Id in Another Profile",envelopeMessage2.getData());
    }

    @Test
    void checkValidUser() {

        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        JwtRequest jwtRequest=new JwtRequest();
        jwtRequest.setEmail(user.getEmail());

        assertTrue(userService.checkValidUser(jwtRequest));
    }

    @Test
    void validatePasswordResetToken()
    {
        String token="test";
        PasswordResetToken passwordResetToken=new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpirationTime(Calendar.getInstance().getTime());

        passwordResetToken.setUser(user);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
        assertEquals("expired",userService.validatePasswordResetToken(token));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(null);
        assertEquals("invalid",userService.validatePasswordResetToken(token));

        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, 15);
        passwordResetToken.setExpirationTime(c.getTime());
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
        assertEquals("valid",userService.validatePasswordResetToken(token));

    }

    @Test
    void signIn_NoUserFound()
    {
        JwtRequest jwtRequest=new JwtRequest();
        jwtRequest.setEmail("user@gmail.com");
        jwtRequest.setPassword("123456");
        MockHttpServletResponse response = new MockHttpServletResponse();

        EnvelopeMessage envelopeMessage=userService.signIn(jwtRequest,response).getBody();
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

        EnvelopeMessage envelopeMessage=userService.signIn(jwtRequest,response).getBody();
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


        Set<Role>roles=new HashSet<>();
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
    void passwordResetTokenMail() throws MessagingException, UnsupportedEncodingException {
        String token = "@@!!@";
        String url = URL_EMAIL_RESETPASSWORD + token;
        assertEquals(url,userServiceImplementation.passwordResetTokenMail(user,token));
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

        String url=userServiceImplementation.passwordResetTokenMail(user,"token");

        assertEquals(url.substring(0,36),userService.resetPassword(passwordModel,request).substring(0,36));
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

}