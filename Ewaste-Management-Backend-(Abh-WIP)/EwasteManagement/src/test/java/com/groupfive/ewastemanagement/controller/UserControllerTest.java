package com.groupfive.ewastemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.entity.Role;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.PasswordResetDTO;
import com.groupfive.ewastemanagement.dto.request.RefreshTokenDTO;
import com.groupfive.ewastemanagement.dto.request.UserDTO;
import com.groupfive.ewastemanagement.dto.request.SignInRequestDTO;
import com.groupfive.ewastemanagement.service.UserService;
import com.groupfive.ewastemanagement.service.UserSignInService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {

    @InjectMocks
    UserController userController;
    @Mock
    UserService userService;

    @Mock
    UserSignInService userSignInService;

    @Mock
    JwtUtil jwtUtil;

    ObjectMapper objectMapper=new ObjectMapper();

    MockMvc mockMvc;

    @BeforeEach
    public void setup()
    {
        this.mockMvc= MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testSignUp() throws Exception {
        UserDTO userDTO =new UserDTO();
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
        userDTO.setPinCode("560034");
        userDTO.setMatchingPassword("123456");
        userDTO.setAddress1("BTM Layout");

        MockHttpServletRequest request=new MockHttpServletRequest();
        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(userDTO);

        ResponseEntity<ResponseMessage>response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        String content=objectMapper.writeValueAsString(userDTO);
        Mockito.when(userService.registerUser(userDTO,request)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/user").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk());
    }

    @Test
    void testPasswordReset() throws Exception {
        PasswordResetDTO passwordResetDTO =new PasswordResetDTO();
        passwordResetDTO.setEmail("abhinav@dmail.com");

        MockHttpServletRequest request=new MockHttpServletRequest();
        String content=objectMapper.writeValueAsString(passwordResetDTO);
        Mockito.when(userSignInService.resetPassword(passwordResetDTO,request)).thenReturn(new ResponseEntity<>(new ResponseMessage(SUCCESS,"url"),HttpStatus.CREATED));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/password/reset").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isCreated());

    }

    @Test
    void testSaveResetPassword() throws Exception {
        PasswordResetDTO passwordResetDTO =new PasswordResetDTO();
        passwordResetDTO.setEmail("abhinav@dmail.com");
        passwordResetDTO.setNewPassword("abhinav");

        String content=objectMapper.writeValueAsString(passwordResetDTO);
        Mockito.when(userService.savePassword("token", passwordResetDTO)).thenReturn(new ResponseEntity<>(new ResponseMessage(SUCCESS,SUCCESS),HttpStatus.OK));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/password/save").contentType(MediaType.APPLICATION_JSON).content(content).param("token","token")).andExpect(status().isOk());

    }

    @Test
    void testViewProfile() throws Exception {
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        User user=new User();
        user.setEmail("abhinav@dmail.com");
        user.setFirstName("abhinav");
        user.setLastName("singh");
        user.setMobileNo("9898989898");
        user.setCity("Bangalore");
        user.setState("Karnataka");
        user.setPassword("123456");
        user.setPinCode("560034");
        user.setAddress1("BTM Layout");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(user);
        ResponseEntity<ResponseMessage>responseEntity=new ResponseEntity<>(responseMessage,HttpStatus.OK);

        Mockito.when(userService.viewProfile(request)).thenReturn(responseEntity);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/profile/view")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());

    }

    @Test
    void testEditProfile() throws Exception {
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        UserDTO userDTO =new UserDTO();
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
        userDTO.setPinCode("560034");
        userDTO.setMatchingPassword("123456");
        userDTO.setAddress1("BTM Layout");

        User user=new User();
        user.setEmail("abhinav@dmail.com");
        user.setFirstName("abhinav");
        user.setLastName("singh");
        user.setMobileNo("9898989898");
        user.setCity("Bangalore");
        user.setState("Karnataka");
        user.setPassword("123456");
        user.setPinCode("560034");
        user.setAddress1("BTM Layout");

        String content=objectMapper.writeValueAsString(userDTO);


        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(user);
        ResponseEntity<ResponseMessage>responseEntity=new ResponseEntity<>(responseMessage,HttpStatus.OK);

        Mockito.when(userService.editProfile(userDTO,request)).thenReturn(responseEntity);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/profile/edit")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());

    }

    @Test
    void testSignIn() throws Exception {
        MockHttpServletResponse response=new MockHttpServletResponse();
        SignInRequestDTO signInRequestDTO =new SignInRequestDTO();
        signInRequestDTO.setEmail("abhinav@dmail.com");
        signInRequestDTO.setPassword("password");

        String content=objectMapper.writeValueAsString(signInRequestDTO);


        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData("token");
        ResponseEntity<ResponseMessage>responseEntity=new ResponseEntity<>(responseMessage,HttpStatus.OK);

        Mockito.when(userSignInService.signIn(signInRequestDTO,response)).thenReturn(responseEntity);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isOk());

    }

    @Test
    void testSignInGoogle() throws Exception {
        MockHttpServletResponse response=new MockHttpServletResponse();

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData("token");
        ResponseEntity<ResponseMessage>responseEntity=new ResponseEntity<>(responseMessage,HttpStatus.OK);

        Mockito.when(userSignInService.signInWithGoogle("abhinav@dmail.com",response)).thenReturn(responseEntity);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/sign-in/google")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","abhinav@dmail.com")
        ).andExpect(status().isOk());

    }

    @Test
    void testRefreshTokenToken() throws Exception {
        MockHttpServletResponse response=new MockHttpServletResponse();

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData("token");

        RefreshTokenDTO refreshTokenDTO =new RefreshTokenDTO();
        refreshTokenDTO.setToken("token");

        String content=objectMapper.writeValueAsString(refreshTokenDTO);
        ResponseEntity<ResponseMessage>responseEntity=new ResponseEntity<>(responseMessage,HttpStatus.OK);

        Mockito.when(userSignInService.signInWithGoogle("abhinav@dmail.com",response)).thenReturn(responseEntity);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/refresh/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isOk());

    }
}