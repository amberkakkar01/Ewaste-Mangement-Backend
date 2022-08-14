package com.groupfive.ewastemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.SellItems;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.entity.VendorOrders;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.AcceptItemOnSaleDTO;
import com.groupfive.ewastemanagement.service.VendorService;
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

import java.util.ArrayList;
import java.util.List;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VendorControllerTest {

    @InjectMocks
    VendorController vendorController;

    @Mock
    VendorService vendorService;

    @Mock
    JwtUtil jwtUtil;

    ObjectMapper objectMapper=new ObjectMapper();
    MockMvc mockMvc;
    @BeforeEach
    public void setup()
    {
        this.mockMvc= MockMvcBuilders.standaloneSetup(vendorController).build();
    }

    @Test
    void testViewAllItemOnSale() throws Exception {
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");


        SellItems sellItems=new SellItems();
        sellItems.setAvailableQuantity("uid");
        sellItems.setCategory(categoriesAccepted);
        sellItems.setStatus("Available");
        sellItems.setItemName("Item Name");
        sellItems.setCollectorUid("collectorUid");
        sellItems.setQuantity("2");

        List<SellItems>sellItemsList=new ArrayList<>();
        sellItemsList.add(sellItems);

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");
        ResponseMessageWithPagination responseMessage=new ResponseMessageWithPagination();
        responseMessage.setData(sellItemsList);

        ResponseEntity<ResponseMessageWithPagination> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(vendorService.viewAllItemOnSale(1,1)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/view/items")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo","1").param("pageSize","1")
                .header("Authorization","googleToken")
        ).andExpect(status().isCreated());
    }

    @Test
    void testPurchaseItemsOnSale() throws Exception {
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Category");

        AcceptItemOnSaleDTO acceptItemOnSaleDTO =new AcceptItemOnSaleDTO();
        acceptItemOnSaleDTO.setQuantity("10");

        SellItems sellItems=new SellItems();
        sellItems.setAvailableQuantity("uid");
        sellItems.setCategory(categoriesAccepted);
        sellItems.setStatus("Available");
        sellItems.setItemName("Item Name");
        sellItems.setCollectorUid("collectorUid");
        sellItems.setQuantity("2");

        List<SellItems>sellItemsList=new ArrayList<>();
        sellItemsList.add(sellItems);

        String content=objectMapper.writeValueAsString(acceptItemOnSaleDTO);

        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");
        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(sellItemsList);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(vendorService.purchaseItemsOnSale(acceptItemOnSaleDTO,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/items/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testPurchaseItemsSummary() throws Exception {

        VendorOrders vendorOrders=new VendorOrders();
        vendorOrders.setAddress("categoriesAccepted");
        vendorOrders.setStatus("Available");
        vendorOrders.setDate("Item Name");
        vendorOrders.setQuantity("2");

        List<VendorOrders>sellItemsList=new ArrayList<>();
        sellItemsList.add(vendorOrders);


        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(EMAIL,"abhinav@dmail.com");
        request.addHeader(AUTHORIZATION,"Bearer token");
        ResponseMessage responseMessage=new ResponseMessage();
        responseMessage.setData(sellItemsList);

        ResponseEntity<ResponseMessage> response=new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        Mockito.when(vendorService.orderSummary(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/purchase/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testViewCollectorProfileInPurchaseSummary() throws Exception {

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

        Mockito.when(vendorService.viewCollectorProfileInSummary(1L,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/purchase-summary/collector-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .param("user_id","1")
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

    @Test
    void testViewCollectorProfileInSale() throws Exception {

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

        Mockito.when(vendorService.viewCollectorProfile(1L,request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/view/items/collector-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .param("user_id","1")
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

        Mockito.when(vendorService.getAnalytics(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/vendor/analytics/view-collector")
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

        Mockito.when(vendorService.getAnalyticsV2(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/vendor/analytics/v2")
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

        Mockito.when(vendorService.getAnalyticsV4(request)).thenReturn(response);
        when(jwtUtil.fetchId("googleToken")).thenReturn("abhinav@dmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/vendor/analytics/v4")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","googleToken")
        ).andExpect(status().isOk());
    }

}