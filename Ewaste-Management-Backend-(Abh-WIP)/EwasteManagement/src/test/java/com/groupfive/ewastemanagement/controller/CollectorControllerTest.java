package com.groupfive.ewastemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.AllPendingRequest;
import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.EWasteDrive;
import com.groupfive.ewastemanagement.entity.SellItems;
import com.groupfive.ewastemanagement.entity.Notification;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.EWasteDriveDTO;
import com.groupfive.ewastemanagement.dto.request.SellItemDTO;
import com.groupfive.ewastemanagement.service.CollectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CollectorControllerTest {

    @InjectMocks
    CollectorController collectorController;

    @Mock
    CollectorService collectorService;

    @Mock
    JwtUtil jwtUtil;

    ObjectMapper objectMapper=new ObjectMapper();
    MockMvc mockMvc;
    @BeforeEach
    public void setup()
    {
        this.mockMvc= MockMvcBuilders.standaloneSetup(collectorController).build();
    }

    @Test
    void testViewAllPendingRequest() throws Exception {
        AllPendingRequest allPendingRequest=new AllPendingRequest();
        allPendingRequest.setItemName("Item");

        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        allPendingRequest.setCategory(categoriesAccepted);
        allPendingRequest.setQuantity("2");
        allPendingRequest.setScheduleDate("date");
        allPendingRequest.setScheduledTime("time");
        allPendingRequest.setRequestType("pickUp");
        allPendingRequest.setStatus("Available");

        List<AllPendingRequest>allPendingRequestList=new ArrayList<>();
        allPendingRequestList.add(allPendingRequest);

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessageWithPagination responseMessage=new ResponseMessageWithPagination();
        responseMessage.setData(allPendingRequestList);

        ResponseEntity<ResponseMessageWithPagination> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.getPendingRequest(1,1,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/request/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo","1")
                .param("pageSize","1")
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testViewRequestSummary() throws Exception {
        AllPendingRequest allPendingRequest=new AllPendingRequest();
        allPendingRequest.setItemName("Item");

        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        allPendingRequest.setCategory(categoriesAccepted);
        allPendingRequest.setQuantity("2");
        allPendingRequest.setScheduleDate("date");
        allPendingRequest.setScheduledTime("time");
        allPendingRequest.setRequestType("pickUp");
        allPendingRequest.setStatus("Available");

        List<AllPendingRequest>allPendingRequestList=new ArrayList<>();
        allPendingRequestList.add(allPendingRequest);

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessageWithPagination responseMessage=new ResponseMessageWithPagination();
        responseMessage.setData(allPendingRequestList);

        ResponseEntity<ResponseMessageWithPagination> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.getRequestSummary(1,1,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/request/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo","1")
                .param("pageSize","1")
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testAcceptPendingRequest() throws Exception {
        AllPendingRequest allPendingRequest=new AllPendingRequest();
        allPendingRequest.setItemName("Item");

        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        allPendingRequest.setCategory(categoriesAccepted);
        allPendingRequest.setQuantity("2");
        allPendingRequest.setScheduleDate("date");
        allPendingRequest.setScheduledTime("time");
        allPendingRequest.setRequestType("pickUp");
        allPendingRequest.setStatus("Available");

        List<AllPendingRequest>allPendingRequestList=new ArrayList<>();
        allPendingRequestList.add(allPendingRequest);

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(allPendingRequestList);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.acceptPendingRequest("1",request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/request/accept-pending")
                .contentType(MediaType.APPLICATION_JSON)
                .param("order","1")
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testOrganizeDrive() throws Exception {
        EWasteDriveDTO eWasteDriveDTO = new EWasteDriveDTO();
        eWasteDriveDTO.setDriveName("Demo Drive");
        eWasteDriveDTO.setDescription("Demo Drive");

        CategoriesAccepted categoriesAccepted = new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("IT Appliances");

        Set<CategoriesAccepted> set=new HashSet<>();
        set.add(categoriesAccepted);

        eWasteDriveDTO.setCategoryAcceptedSet(set);
        eWasteDriveDTO.setOrganizerName("abhinav");
        eWasteDriveDTO.setDate("2022-08-23");
        eWasteDriveDTO.setTime("07:23");
        eWasteDriveDTO.setLocation("BLR");
        eWasteDriveDTO.setStatus(UPCOMING);

        //Drive Creation

        EWasteDrive eWasteDrive = new EWasteDrive();
        eWasteDrive.setDriveName(eWasteDriveDTO.getDriveName());
        eWasteDrive.setDescription(eWasteDriveDTO.getDescription());
        eWasteDrive.setOrganizerName(eWasteDriveDTO.getOrganizerName());

        eWasteDrive.setEWasteCategoryAccepted(eWasteDriveDTO.getCategoryAcceptedSet());
        eWasteDrive.setDate(eWasteDriveDTO.getDate());
        eWasteDrive.setTime(eWasteDriveDTO.getTime());
        eWasteDrive.setLocation(eWasteDriveDTO.getLocation());
        eWasteDrive.setStatus(eWasteDriveDTO.getStatus());


        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        String content=objectMapper.writeValueAsString(eWasteDriveDTO);

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(eWasteDrive);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.organizeDrive(eWasteDriveDTO,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/organize-drive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization","googleToken")
        ).andExpect(status().isCreated());
    }

    @Test
    void testViewOrganizedDrive() throws Exception {
        EWasteDriveDTO eWasteDriveDTO = new EWasteDriveDTO();
        eWasteDriveDTO.setDriveName("Demo Drive");
        eWasteDriveDTO.setDescription("Demo Drive");

        CategoriesAccepted categoriesAccepted = new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("IT Appliances");

        Set<CategoriesAccepted> set=new HashSet<>();
        set.add(categoriesAccepted);

        eWasteDriveDTO.setCategoryAcceptedSet(set);
        eWasteDriveDTO.setDate("2022-08-23");
        eWasteDriveDTO.setTime("07:23");
        eWasteDriveDTO.setLocation("BLR");
        eWasteDriveDTO.setStatus(UPCOMING);

        //Drive Creation

        EWasteDrive eWasteDrive = new EWasteDrive();
        eWasteDrive.setDriveName(eWasteDriveDTO.getDriveName());
        eWasteDrive.setDescription(eWasteDriveDTO.getDescription());
        eWasteDrive.setOrganizerName(eWasteDriveDTO.getOrganizerName());

        eWasteDrive.setEWasteCategoryAccepted(eWasteDriveDTO.getCategoryAcceptedSet());
        eWasteDrive.setDate(eWasteDriveDTO.getDate());
        eWasteDrive.setTime(eWasteDriveDTO.getTime());
        eWasteDrive.setLocation(eWasteDriveDTO.getLocation());
        eWasteDrive.setStatus(eWasteDriveDTO.getStatus());


        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessageWithPagination responseMessage=new ResponseMessageWithPagination();
        responseMessage.setData(eWasteDrive);

        ResponseEntity<ResponseMessageWithPagination> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.viewMyDrive(1,1,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/my-drive")
                .param("pageNo","1").param("pageSize","1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }


    @Test
    void testSellE_WASTE() throws Exception {
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        SellItemDTO sellItemDTO = new SellItemDTO();
        sellItemDTO.setItemName("TV");
        sellItemDTO.setCategory(categoriesAccepted);
        sellItemDTO.setStatus(AVAILABLE);
        sellItemDTO.setPrice("1200");
        sellItemDTO.setId(1L);
        sellItemDTO.setQuantity("12");

        SellItems sellItems=new SellItems();
        sellItems.setAvailableQuantity("uid");
        sellItems.setCategory(categoriesAccepted);
        sellItems.setStatus("Available");
        sellItems.setItemName("Item Name");
        sellItems.setCollectorUid("collectorUid");
        sellItems.setQuantity("2");

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        String content=objectMapper.writeValueAsString(sellItemDTO);

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(sellItems);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.sellItem(sellItemDTO,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization","googleToken")
        ).andExpect(status().isCreated());
    }

    @Test
    void testSellItemAvailableSummary() throws Exception {
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        SellItems sellItems=new SellItems();
        sellItems.setAvailableQuantity("uid");
        sellItems.setCategory(categoriesAccepted);
        sellItems.setStatus("Available");
        sellItems.setItemName("Item Name");
        sellItems.setCollectorUid("collectorUid");
        sellItems.setQuantity("2");

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(sellItems);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.sellItemAvailableSummary(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/available/sell-summary")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testSellItemSoldSummary() throws Exception {
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        SellItems sellItems=new SellItems();
        sellItems.setAvailableQuantity("uid");
        sellItems.setCategory(categoriesAccepted);
        sellItems.setStatus("Available");
        sellItems.setItemName("Item Name");
        sellItems.setCollectorUid("collectorUid");
        sellItems.setQuantity("2");

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(sellItems);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.sellItemSoldSummary(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/sold/sell-summary")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testModifySellItemAvailableSummary() throws Exception {
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        SellItemDTO sellItemDTO = new SellItemDTO();
        sellItemDTO.setItemName("TV");
        sellItemDTO.setCategory(categoriesAccepted);
        sellItemDTO.setStatus(AVAILABLE);
        sellItemDTO.setPrice("1200");
        sellItemDTO.setId(1L);
        sellItemDTO.setQuantity("12");

        SellItems sellItems=new SellItems();
        sellItems.setAvailableQuantity("uid");
        sellItems.setCategory(categoriesAccepted);
        sellItems.setStatus("Available");
        sellItems.setItemName("Item Name");
        sellItems.setCollectorUid("collectorUid");
        sellItems.setQuantity("2");

        String content=objectMapper.writeValueAsString(sellItemDTO);

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(sellItems);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.editSellItemAvailableSummary(request, sellItemDTO)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/available/sell-summary")
                .contentType(MediaType.APPLICATION_JSON).content(content)
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

        Mockito.when(collectorService.viewNotification(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/collector/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
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

        Mockito.when(collectorService.readNotification(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/collector/notification/mark-as-read")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testModifyOrganizedDrive() throws Exception {
        EWasteDriveDTO eWasteDriveDTO = new EWasteDriveDTO();
        eWasteDriveDTO.setDriveName("Demo Drive");
        eWasteDriveDTO.setDescription("Demo Drive");

        CategoriesAccepted categoriesAccepted = new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("IT Appliances");

        Set<CategoriesAccepted> set=new HashSet<>();
        set.add(categoriesAccepted);

        eWasteDriveDTO.setCategoryAcceptedSet(set);
        eWasteDriveDTO.setDate("2022-08-23");
        eWasteDriveDTO.setTime("07:23");
        eWasteDriveDTO.setLocation("BLR");
        eWasteDriveDTO.setStatus(UPCOMING);

        //Drive Creation

        EWasteDrive eWasteDrive = new EWasteDrive();
        eWasteDrive.setDriveName(eWasteDriveDTO.getDriveName());
        eWasteDrive.setDescription(eWasteDriveDTO.getDescription());
        eWasteDrive.setOrganizerName(eWasteDriveDTO.getOrganizerName());

        eWasteDrive.setEWasteCategoryAccepted(eWasteDriveDTO.getCategoryAcceptedSet());
        eWasteDrive.setDate(eWasteDriveDTO.getDate());
        eWasteDrive.setTime(eWasteDriveDTO.getTime());
        eWasteDrive.setLocation(eWasteDriveDTO.getLocation());
        eWasteDrive.setStatus(eWasteDriveDTO.getStatus());


        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(eWasteDrive);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.editDriveSummary(1L,"Available",request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/my-drive")
                .param("pageNo","1").param("id","1").param("status","Available")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }
    @Test
    void testViewCustomerProfile() throws Exception {

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

        Mockito.when(collectorService.viewCustomerProfile("uid")).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/view/customer-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id","uid")
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

        Mockito.when(collectorService.getAnalytics(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/collector/analytics/e-waste-drives")
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

        Mockito.when(collectorService.getAnalyticsV2(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/collector/analytics/category-accepted")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testAnalyticsV4() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(null);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.getAnalyticsV4(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/collector/analytics/view-vendors")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testAnalyticsV5() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(null);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.getAnalyticsV5(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/collector/analytics/view-customer")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testAnalyticsV6() throws Exception {

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");

        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(null);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(collectorService.getAnalyticsV6(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/collector/analytics/view-collector")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

}
