package com.groupfive.ewastemanagement.service.userservice;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.UserDetails;
import com.groupfive.ewastemanagement.entity.Role;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.PasswordResetDTO;
import com.groupfive.ewastemanagement.dto.request.RefreshTokenDTO;
import com.groupfive.ewastemanagement.dto.request.UserDTO;
import com.groupfive.ewastemanagement.dto.request.SignInRequestDTO;
import com.groupfive.ewastemanagement.repository.UserDetailsRepo;
import com.groupfive.ewastemanagement.repository.RoleRepo;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.security.CustomUserDetailsService;
import com.groupfive.ewastemanagement.service.UserSignInService;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetails;
import com.groupfive.ewastemanagement.service.UserService;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetailsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.security.auth.login.AccountLockedException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserSignInService userSignInService;

    @MockBean
    UserRepo userRepo;

    @MockBean
    JWTUserDetailsService userDetailsService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    UserDetailsRepo userDetailsRepo;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    RoleRepo roleRepo;

    User user;

    UserDTO userDTO;
    User customer;

    User collector;

    User vendor;

    UserDetails userDetails;

    @BeforeAll
    public void setUp()
    {
        userDTO = new UserDTO();
        userDTO.setEmail("abhinav@dmail.com");
        userDTO.setFirstName("abhinav");
        userDTO.setLastName("singh");
        userDTO.setMobileNo("9898989898");

        Role role = new Role();
        role.setName("CUSTOMER");

        userDTO.setRole(role);
        userDTO.setCity("Bangalore");
        userDTO.setState("Karnataka");
        userDTO.setPassword("123456");
        userDTO.setMatchingPassword("123456");
        userDTO.setAddress1("BTM Layout");

        user=new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setMobileNo(userDTO.getMobileNo());
        user.setPassword(userDTO.getPassword());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setAddress1(userDTO.getAddress1());

        customer=new User();
        customer.setFirstName(userDTO.getFirstName());
        customer.setLastName(userDTO.getLastName());
        customer.setEmail(userDTO.getEmail());
        customer.setMobileNo(userDTO.getMobileNo());
        customer.setPassword(userDTO.getPassword());
        customer.setCity(userDTO.getCity());
        customer.setState(userDTO.getState());
        customer.setAddress1(userDTO.getAddress1());

        Set<Role>roles=new HashSet<>();
        Role role1=new Role();
        role1.setName(CUSTOMER);
        roles.add(role1);

        customer.setRoles(roles);

        collector=new User();
        collector.setFirstName(userDTO.getFirstName());
        collector.setLastName(userDTO.getLastName());
        collector.setEmail(userDTO.getEmail());
        collector.setMobileNo(userDTO.getMobileNo());
        collector.setPassword(userDTO.getPassword());
        collector.setCity(userDTO.getCity());
        collector.setState(userDTO.getState());
        collector.setAddress1(userDTO.getAddress1());

        Set<Role>rolesCollector=new HashSet<>();
        Role roleCollector=new Role();
        role1.setName(COLLECTOR);
        roles.add(roleCollector);

        collector.setRoles(rolesCollector);

        vendor = new User();
        vendor.setLastName(userDTO.getLastName());
        vendor.setEmail(userDTO.getEmail());
        vendor.setMobileNo(userDTO.getMobileNo());
        vendor.setPassword(userDTO.getPassword());
        vendor.setCity(userDTO.getCity());
        vendor.setState(userDTO.getState());
        vendor.setAddress1(userDTO.getAddress1());

        Set<Role>rolesVendor=new HashSet<>();
        Role roleVendor=new Role();
        role1.setName(VENDOR);
        roles.add(roleVendor);

        vendor.setRoles(rolesVendor);

        userDetails=new UserDetails();
    }

    @Test
    void FIND_THE_USER_BY_HIS_EMAIL_ID(){
        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn(customer);
        assertEquals(customer,userService.findUserByEmail(customer.getEmail()));
    }

    @Test
    void WHEN_A_USER_SAVES_NEW_PASSWORD_SUCCESS(){
        String token="random";
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(customer.getEmail());
        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn(customer);
        ResponseMessage responseMessage=userService.savePassword(token,new PasswordResetDTO()).getBody();

        assertEquals(SUCCESS,responseMessage.getStatus());
    }

    @Test
    void WHEN_A_USER_SAVES_NEW_PASSWORD_FAILURE(){
        String token="random";
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(customer.getEmail());
        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn(null);

        ResponseMessage responseMessage=userService.savePassword(token,new PasswordResetDTO()).getBody();

        assertEquals(NO_USER_FOUND,responseMessage.getData());
    }

    @Test
    void WHEN_A_USER_RESET_PASSWORD_FAILURE(){
        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn(customer);
        PasswordResetDTO passwordResetDTO =new PasswordResetDTO();

        when(customUserDetailsService.loadUserByUsername(customer.getEmail())).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> userSignInService.resetPassword(passwordResetDTO,request)
        );

        assertEquals("User not found in records", exception.getMessage());
    }

    @Test
    void WHEN_A_USER_RESET_PASSWORD_SUCCESS() throws MessagingException, UnsupportedEncodingException {
        String token="null";
        MockHttpServletRequest request=new MockHttpServletRequest();

        PasswordResetDTO passwordResetDTO =new PasswordResetDTO();
        passwordResetDTO.setEmail(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn(customer);
        JWTUserDetails userDetails;

        userDetails=new JWTUserDetails(customer);

        when(customUserDetailsService.loadUserByUsername(customer.getEmail())).thenReturn(userDetails);
        when(jwtUtil.generatePasswordResetToken(userDetails)).thenReturn(token);

        ResponseMessage responseMessage=userSignInService.resetPassword(passwordResetDTO, request).getBody();
        assertEquals(URL_EMAIL_RESET_PASSWORD
                + token,responseMessage.getData());
    }

    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_CUSTOMER_Success() {
        Role role=new Role();
        role.setName(CUSTOMER);

        userDTO.setRole(role);
        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);
        when(roleRepo.findRoleByName(role.getName())).thenReturn(role);

        ResponseMessage responseMessage1 = userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        User user1= (User) responseMessage1.getData();
        assertEquals(customer.getEmail(), user1.getEmail());

    }

    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_CUSTOMER_Success_WhenNoCustomerIsRegistered() {
        Role role=new Role();
        role.setName(CUSTOMER);

        userDTO.setRole(role);
        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);
        when(roleRepo.findRoleByName(role.getName())).thenReturn(null);

        ResponseMessage responseMessage1 = userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        User user1= (User) responseMessage1.getData();
        assertEquals(customer.getEmail(), user1.getEmail());

    }
    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_COLLECTOR_Failure_WHEN_COLLECTOR_IS_ALREADY_REGISTERED() {

        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);

        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(collector);

        ResponseMessage responseMessage1 = userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        assertEquals("Already Registered with same E-Mail Id", responseMessage1.getData());
    }
    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_COLLECTOR_Success() {
        Role role=new Role();
        role.setName(COLLECTOR);

        userDTO.setRole(role);
        Set<CategoriesAccepted>set=new HashSet<>();
        CategoriesAccepted categoriesAcceptedTemp=new CategoriesAccepted();
        categoriesAcceptedTemp.setId(1L);
        categoriesAcceptedTemp.setCategoryAccepted("Temp");

        CategoriesAccepted categoriesAcceptedLamps=new CategoriesAccepted();
        categoriesAcceptedLamps.setId(2L);
        categoriesAcceptedLamps.setCategoryAccepted("Lamps");

        CategoriesAccepted categoriesAcceptedLargeEquip=new CategoriesAccepted();
        categoriesAcceptedLargeEquip.setId(3L);
        categoriesAcceptedLargeEquip.setCategoryAccepted("LargeEquip");

        CategoriesAccepted categoriesAcceptedSmallEquip=new CategoriesAccepted();
        categoriesAcceptedSmallEquip.setId(4L);
        categoriesAcceptedSmallEquip.setCategoryAccepted("SmallEquip");

        CategoriesAccepted categoriesAcceptedSmallIT=new CategoriesAccepted();
        categoriesAcceptedSmallIT.setId(5L);
        categoriesAcceptedSmallEquip.setCategoryAccepted("SmallIT");

        CategoriesAccepted categoriesAcceptedScreens=new CategoriesAccepted();
        categoriesAcceptedScreens.setId(6L);
        categoriesAcceptedScreens.setCategoryAccepted("Screens");

        set.add(categoriesAcceptedTemp);
        set.add(categoriesAcceptedLamps);
        set.add(categoriesAcceptedLargeEquip);
        set.add(categoriesAcceptedSmallEquip);
        set.add(categoriesAcceptedSmallIT);
        set.add(categoriesAcceptedScreens);
        userDTO.setCategoriesAcceptedSet(set);

        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);

        ResponseMessage responseMessage1 = userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        User user1= (User) responseMessage1.getData();
        assertEquals(collector.getEmail(), user1.getEmail());

    }
    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_COLLECTOR_Success_WHEN_NO_COLLECTOR_REGISTERED() {
        Role role=new Role();
        role.setName(COLLECTOR);

        userDTO.setRole(role);
        Set<CategoriesAccepted>set=new HashSet<>();
        CategoriesAccepted categoriesAcceptedTemp=new CategoriesAccepted();
        categoriesAcceptedTemp.setId(1L);
        categoriesAcceptedTemp.setCategoryAccepted("Temp");

        CategoriesAccepted categoriesAcceptedLamps=new CategoriesAccepted();
        categoriesAcceptedLamps.setId(2L);
        categoriesAcceptedLamps.setCategoryAccepted("Lamps");

        CategoriesAccepted categoriesAcceptedLargeEquip=new CategoriesAccepted();
        categoriesAcceptedLargeEquip.setId(3L);
        categoriesAcceptedLargeEquip.setCategoryAccepted("LargeEquip");

        CategoriesAccepted categoriesAcceptedSmallEquip=new CategoriesAccepted();
        categoriesAcceptedSmallEquip.setId(4L);
        categoriesAcceptedSmallEquip.setCategoryAccepted("SmallEquip");

        CategoriesAccepted categoriesAcceptedSmallIT=new CategoriesAccepted();
        categoriesAcceptedSmallIT.setId(5L);
        categoriesAcceptedSmallEquip.setCategoryAccepted("SmallIT");

        CategoriesAccepted categoriesAcceptedScreens=new CategoriesAccepted();
        categoriesAcceptedScreens.setId(6L);
        categoriesAcceptedScreens.setCategoryAccepted("Screens");

        set.add(categoriesAcceptedTemp);
        set.add(categoriesAcceptedLamps);
        set.add(categoriesAcceptedLargeEquip);
        set.add(categoriesAcceptedSmallEquip);
        set.add(categoriesAcceptedSmallIT);
        set.add(categoriesAcceptedScreens);
        userDTO.setCategoriesAcceptedSet(set);

        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);
        when(roleRepo.findRoleByName(COLLECTOR)).thenReturn(role);

        ResponseMessage responseMessage1 = userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        User user1= (User) responseMessage1.getData();
        assertEquals(collector.getEmail(), user1.getEmail());

    }
    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_COLLECTOR_Success_WHEN_NO_COLLECTOR_IS_REGISTERED() {
        Role role=new Role();
        role.setName(COLLECTOR);

        userDTO.setRole(role);
        Set<CategoriesAccepted>set=new HashSet<>();
        CategoriesAccepted categoriesAcceptedTemp=new CategoriesAccepted();
        categoriesAcceptedTemp.setId(1L);
        categoriesAcceptedTemp.setCategoryAccepted("Temp");

        CategoriesAccepted categoriesAcceptedLamps=new CategoriesAccepted();
        categoriesAcceptedLamps.setId(2L);
        categoriesAcceptedLamps.setCategoryAccepted("Lamps");

        CategoriesAccepted categoriesAcceptedLargeEquip=new CategoriesAccepted();
        categoriesAcceptedLargeEquip.setId(3L);
        categoriesAcceptedLargeEquip.setCategoryAccepted("LargeEquip");

        CategoriesAccepted categoriesAcceptedSmallEquip=new CategoriesAccepted();
        categoriesAcceptedSmallEquip.setId(4L);
        categoriesAcceptedSmallEquip.setCategoryAccepted("SmallEquip");

        CategoriesAccepted categoriesAcceptedSmallIT=new CategoriesAccepted();
        categoriesAcceptedSmallIT.setId(5L);
        categoriesAcceptedSmallEquip.setCategoryAccepted("SmallIT");

        CategoriesAccepted categoriesAcceptedScreens=new CategoriesAccepted();
        categoriesAcceptedScreens.setId(6L);
        categoriesAcceptedScreens.setCategoryAccepted("Screens");

        set.add(categoriesAcceptedTemp);
        set.add(categoriesAcceptedLamps);
        set.add(categoriesAcceptedLargeEquip);
        set.add(categoriesAcceptedSmallEquip);
        set.add(categoriesAcceptedSmallIT);
        set.add(categoriesAcceptedScreens);
        userDTO.setCategoriesAcceptedSet(set);

        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);

        when(roleRepo.findRoleByName(COLLECTOR)).thenReturn(null);
        ResponseMessage responseMessage1 = userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        User user1= (User) responseMessage1.getData();
        assertEquals(collector.getEmail(), user1.getEmail());

    }

    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_VENDOR_Failure_WHEN_COLLECTOR_IS_ALREADY_REGISTERED() {

        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);

        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(vendor);

        ResponseMessage responseMessage1 =  userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        assertEquals("Already Registered with same E-Mail Id", responseMessage1.getData());
    }
    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_VENDOR_Success() {
        Role role=new Role();
        role.setName(VENDOR);

        userDTO.setRole(role);
        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);

        ResponseMessage responseMessage1 = userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        User user1= (User) responseMessage1.getData();
        assertEquals(vendor.getEmail(), user1.getEmail());

    }
    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_VENDOR_Success_WHEN_VENDOR_IS_REGISTERED() {
        Role role=new Role();
        role.setName(VENDOR);

        userDTO.setRole(role);
        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);
        when(roleRepo.findRoleByName(COLLECTOR)).thenReturn(null);

        ResponseMessage responseMessage1 = userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        User user1= (User) responseMessage1.getData();
        assertEquals(vendor.getEmail(), user1.getEmail());

    }
    @Test
    void WHEN_A_USER_WANT_TO_REGISTER_AS_VENDOR_Success_WHEN_NO_VENDOR_IS_REGISTERED() {
        Role role=new Role();
        role.setName(VENDOR);

        userDTO.setRole(role);
        MockHttpServletRequest request=new MockHttpServletRequest();
        when(userRepo.findUserByEmail(userDTO.getEmail())).thenReturn(null);
        when(roleRepo.findRoleByName(VENDOR)).thenReturn(role);

        ResponseMessage responseMessage1 = userService.registerUser(userDTO,request).getBody();

        assert responseMessage1 != null;
        User user1= (User) responseMessage1.getData();
        assertEquals(vendor.getEmail(), user1.getEmail());

    }


    @Test
    void sendResetPasswordMail() throws MessagingException, UnsupportedEncodingException {
        userSignInService.sendResetPasswordMail(user,"hello");
        assertEquals("User Not Found",USER_NOT_FOUND);
    }

    @Test
    void WHEN_A_USER_WANT_TO_SIGNIN_AND_GETS_NoUserFound_ERROR() throws AccountLockedException {
        SignInRequestDTO signInRequestDTO =new SignInRequestDTO();
        signInRequestDTO.setEmail("user@gmail.com");
        signInRequestDTO.setPassword("123456");
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseMessage responseMessage = userSignInService.signIn(signInRequestDTO,response).getBody();
        assert responseMessage != null;
        assertEquals("No user Found", responseMessage.getData());
    }

    @Test
    void WHEN_A_USER_WANT_TO_SIGN_IN() throws AccountLockedException {
        SignInRequestDTO signInRequestDTO =new SignInRequestDTO();
        signInRequestDTO.setEmail(user.getEmail());
        signInRequestDTO.setPassword(user.getPassword());

        Authentication authentication1=mock(Authentication.class);
        authentication1.setAuthenticated(true);

        MockHttpServletResponse response=new MockHttpServletResponse();

        JWTUserDetails userDetails;

        userDetails=new JWTUserDetails(user);

        when(authenticationManager.authenticate(any())).thenReturn(authentication1);

        HashMap<String,String>hashMap=new HashMap<>();
        hashMap.put("Access-Token","123456");

        when(jwtUtil.generateToken(userDetails)).thenReturn(hashMap);
        when(userDetailsService.loadUserByUsername((user.getEmail()))).thenReturn(userDetails);
        when(userRepo.findUserByEmail(user.getEmail())).thenReturn(user);

        ResponseMessage responseMessage = userSignInService.signIn(signInRequestDTO,response).getBody();
        assert responseMessage != null;
        HashMap<String,String>jwtResponse= (HashMap<String, String>) responseMessage.getData();
        assertEquals(hashMap,jwtResponse);
    }

    @Test
    void REFRESH_TOKEN(){
        RefreshTokenDTO refreshTokenDTO =new RefreshTokenDTO();
        refreshTokenDTO.setToken("token token");
        String userName="abhinav@dmail.com";
        when(jwtUtil.getUsernameFromToken(refreshTokenDTO.getToken())).thenReturn(userName);
        Map<String ,String >token=new HashMap<>();
        token.put("Access-Token", refreshTokenDTO.getToken());
        when(userRepo.findUserByEmail(userName)).thenReturn(user);

        when(jwtUtil.createTokenFromUserName(userName,user)).thenReturn(token);
        ResponseMessage responseMessage=userService.refreshToken(refreshTokenDTO).getBody();
        Map<String ,String>map= (Map<String, String>) responseMessage.getData();
        assertEquals(token.get("Access-Token"),map.get("Access-Token"));
    }
    @Test
    void WHEN_A_USER_WANT_TO_SIGN_IN_WITH_GOOGLE()
    {
        when(userRepo.findUserByEmail(user.getEmail())).thenReturn(user);

        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(true);

        MockHttpServletResponse response=new MockHttpServletResponse();

        JWTUserDetails userDetails;

        userDetails=new JWTUserDetails(user);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        HashMap<String,String>hashMap=new HashMap<>();
        hashMap.put("Access-Token","123456");

        when(jwtUtil.generateToken(userDetails)).thenReturn(hashMap);
        when(userDetailsService.loadUserByUsername((user.getEmail()))).thenReturn(userDetails);

        ResponseMessage responseMessage = userSignInService.signInWithGoogle(user.getEmail(), response).getBody();
        assert responseMessage != null;
        HashMap<String,String> jwtResponse= (HashMap<String, String>) responseMessage.getData();
        assertEquals(hashMap,jwtResponse);
    }

    @Test
    void WHEN_A_USER_WANT_TO_SIGN_IN_WITH_GOOGLE_Failure()
    {
        when(userRepo.findUserByEmail(user.getEmail())).thenReturn(null);

        MockHttpServletResponse response=new MockHttpServletResponse();

        ResponseMessage responseMessage = userSignInService.signInWithGoogle(user.getEmail(), response).getBody();
        assert responseMessage != null;
        assertEquals(NO_USER_FOUND,responseMessage.getData());
    }


    @Test
    void WHEN_A_USER_WANT_TO_VIEW_HIS_PROFILE() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        ResponseMessage responseMessage =userService.viewProfile(request).getBody();

        assert responseMessage != null;
        User c=(User) responseMessage.getData();

        assertEquals(customer.getEmail(), c.getEmail());
    }

    @Test
    void WHEN_A_USER_WANT_TO_VIEW_HIS_PROFILE_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> userService.viewProfile(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_A_USER_WANT_TO_VIEW_HIS_PROFILE_Failure()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(null);
        ResponseMessage responseMessage = userService.viewProfile(request).getBody();


        assert responseMessage != null;
        assertEquals(NO_SUCH_USER_FOUND,responseMessage.getData());

    }
    @Test
    void WHEN_A_USER_WANT_TO_EDIT_HIS_PROFILE_CUSTOMER() {
        Role role=new Role();
        role.setName(CUSTOMER);

        Set<Role>roleSet=new HashSet<>();
        roleSet.add(role);

        userDTO.setRole(role);
        User customer= new User();
        customer.setFirstName(userDTO.getFirstName());
        customer.setLastName(userDTO.getLastName());
        customer.setEmail(userDTO.getEmail());
        customer.setCity(userDTO.getCity());
        customer.setState(userDTO.getState());
        customer.setAddress1(userDTO.getAddress1());
        customer.setMobileNo(userDTO.getMobileNo());
        customer.setPassword(userDTO.getPassword());
        customer.setPinCode(userDTO.getPinCode());
        customer.setUid(customer.getUid());
        customer.setRoles(roleSet);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        ResponseMessage responseMessage = userService.editProfile(userDTO,request).getBody();

        assert responseMessage != null;
        User c=(User) responseMessage.getData();

        assertEquals(customer.getEmail(), c.getEmail());
    }

    @Test
    void WHEN_A_USER_WANT_TO_EDIT_HIS_PROFILE_COLLECTOR() {
        Role role=new Role();
        role.setName(COLLECTOR);

        Set<Role>roleSet=new HashSet<>();
        roleSet.add(role);
        collector.setRoles(roleSet);

        userDTO.setRole(role);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        userDetails.setUser(collector);

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        when(userDetailsRepo.findUserByUid(collector.getUid())).thenReturn(userDetails);

        ResponseMessage responseMessage = userService.editProfile(userDTO,request).getBody();

        assert responseMessage != null;
        User c=(User) responseMessage.getData();

        assertEquals(customer.getEmail(), c.getEmail());
    }

    @Test
    void WHEN_A_USER_WANT_TO_EDIT_HIS_PROFILE_COLLECTOR_Branching() {
        Role role=new Role();
        role.setName(COLLECTOR);

        Set<Role>roleSet=new HashSet<>();
        roleSet.add(role);
        collector.setRoles(roleSet);

        userDTO.setRole(role);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        userDetails.setUser(collector);

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        role.setName(CUSTOMER);

        when(userDetailsRepo.findUserByUid(collector.getUid())).thenReturn(userDetails);

        ResponseMessage responseMessage = userService.editProfile(userDTO,request).getBody();

        assert responseMessage != null;
        User c=(User) responseMessage.getData();

        assertEquals(customer.getEmail(), c.getEmail());
    }

    @Test
    void WHEN_A_USER_WANT_TO_EDIT_HIS_PROFILE_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> userService.editProfile(userDTO,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void HEN_A_USER_WANT_TO_EDIT_HIS_PROFILE_Failure()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(null);
        ResponseMessage responseMessage =userService.editProfile(userDTO,request).getBody();


        assert responseMessage != null;
        assertEquals(NO_SUCH_USER_FOUND,responseMessage.getData());

    }

}