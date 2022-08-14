package com.groupfive.ewastemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.EWasteDrive;
import com.groupfive.ewastemanagement.entity.Orders;
import com.groupfive.ewastemanagement.entity.Notification;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.RequestDTO;
import com.groupfive.ewastemanagement.service.CustomerService;
import com.groupfive.ewastemanagement.service.OrderService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerControllerTest {

    @InjectMocks
    CustomerController customerController;

    @Mock
    CustomerService customerService;

    @Mock
    OrderService orderService;

    @Mock
    JwtUtil jwtUtil;

    ObjectMapper objectMapper=new ObjectMapper();
    MockMvc mockMvc;
    @BeforeEach
    public void setup()
    {
        this.mockMvc= MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    void testCreatePickUpReq() throws Exception {
        RequestDTO requestDTO =new RequestDTO();
        requestDTO.setItemName("Item");

        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        requestDTO.setCategory(categoriesAccepted);
        requestDTO.setQuantity("2");
        requestDTO.setScheduledDate("date");
        requestDTO.setScheduledTime("time");
        requestDTO.setRequestType("pickUp");


        Orders orders=new Orders();
        orders.setOrderUid("uid");
        orders.setCustomerUid("customerUid");
        orders.setCategory(categoriesAccepted);
        orders.setStatus("Available");
        orders.setItemName("Item Name");
        orders.setCollectorUid("collectorUid");
        orders.setQuantity("2");

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(orders);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        String content=objectMapper.writeValueAsString(requestDTO);

        Mockito.when(orderService.createPickUpRequest(requestDTO,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/pick-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization","googleToken")
        ).andExpect(status().isCreated());
    }

    @Test
    void testCreateDropOffReq() throws Exception {
        RequestDTO requestDTO =new RequestDTO();
        requestDTO.setItemName("Item");

        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        requestDTO.setCategory(categoriesAccepted);
        requestDTO.setQuantity("2");
        requestDTO.setScheduledDate("date");
        requestDTO.setScheduledTime("time");
        requestDTO.setRequestType("pickUp");


        Orders orders=new Orders();
        orders.setOrderUid("uid");
        orders.setCustomerUid("customerUid");
        orders.setCategory(categoriesAccepted);
        orders.setStatus("Available");
        orders.setItemName("Item Name");
        orders.setCollectorUid("collectorUid");
        orders.setQuantity("2");

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");
        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(orders);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        String content=objectMapper.writeValueAsString(requestDTO);

        Mockito.when(orderService.createDropOffRequest(requestDTO,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/drop-off")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization","googleToken")
        ).andExpect(status().isCreated());
    }

    @Test
    void testSearchDropOffLocation() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        Mockito.when(orderService.showDropOffLocation("category",request)).thenReturn(1);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/drop-off/details")
                .contentType(MediaType.APPLICATION_JSON)
                        .param("category","category")
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testSearchDropOffViewCollector() throws Exception {

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

        List<User>users=new ArrayList<>();
        users.add(user);

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(users);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(orderService.viewListOfCollectorsForDropOff("category",request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/drop-off/view-collectors")
                .contentType(MediaType.APPLICATION_JSON)
                .param("category","category")
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testSearchPickUpLocation() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        Mockito.when(orderService.showDropOffLocation("category",request)).thenReturn(1);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/pick-up/view-collectors")
                .contentType(MediaType.APPLICATION_JSON)
                .param("category","category")
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testRequestSummary() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        Orders orders=new Orders();
        orders.setOrderUid("uid");
        orders.setCustomerUid("customerUid");
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");
        orders.setCategory(categoriesAccepted);
        orders.setStatus("Available");
        orders.setItemName("Item Name");
        orders.setCollectorUid("collectorUid");
        orders.setQuantity("2");

        List<Orders>ordersList=new ArrayList<>();
        ordersList.add(orders);
        ResponseMessageWithPagination responseMessage=new ResponseMessageWithPagination();
        responseMessage.setData(ordersList);

        ResponseEntity<ResponseMessageWithPagination> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(customerService.getAllOrders(1,1,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/request/all")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo","1")
                .param("pageSize","1")
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testViewEWasteDrives() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        EWasteDrive eWasteDrive=new EWasteDrive();
        eWasteDrive.setDriveName("uid");
        eWasteDrive.setStatus("customerUid");
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");
        Set<CategoriesAccepted>set=new HashSet<>();
        set.add(categoriesAccepted);
        eWasteDrive.setEWasteCategoryAccepted(set);
        eWasteDrive.setDate("Available");

        List<EWasteDrive>eWasteDriveList=new ArrayList<>();
        eWasteDriveList.add(eWasteDrive);
        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(eWasteDriveList);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(customerService.viewDrives(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/view-drives")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testViewNotification() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        Notification notification=new Notification();
        notification.setMessage("New Notification");

        List<Notification>listNotification=new ArrayList<>();
        listNotification.add(notification);

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(listNotification);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(customerService.viewNotification(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/customer/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testViewCollectorProfile() throws Exception {

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

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(customerService.viewCollectorProfile("uid")).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/request/all/collector-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .param("user_id","uid")
                .header("Authorization","googleToken")
        ).andExpect(status().isCreated());
    }

    @Test
    void testMarkNotificationAsViewed() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        Notification notification=new Notification();
        notification.setMessage("New Notification");

        List<Notification>listNotification=new ArrayList<>();
        listNotification.add(notification);

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(listNotification);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(customerService.readNotification(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/customer/notification/mark-as-read")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testAnalyticsV1() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(null);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(customerService.getAnalytics(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/customer/analytics/view-drives")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testAnalyticsV2() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(null);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(customerService.getAnalyticsV2(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/customer/analytics/view-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());


    }

    @Test
    void testAnalyticsV3() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(null);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(customerService.getAnalyticsForCollectorInCity(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/customer/analytics/view-collector")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());

    }
}