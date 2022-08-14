package com.groupfive.ewastemanagement.service.vendorservice;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.SellItems;
import com.groupfive.ewastemanagement.entity.UserDetails;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.entity.VendorOrders;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.exception.NoDataException;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.AcceptItemOnSaleDTO;
import com.groupfive.ewastemanagement.dto.request.SellItemDTO;
import com.groupfive.ewastemanagement.repository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.SellItemRepo;
import com.groupfive.ewastemanagement.repository.UserDetailsRepo;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.repository.VendorOrdersRepo;
import com.groupfive.ewastemanagement.service.VendorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VendorServiceImplementationTest {

    public static final String AUTHORIZATION_TOKEN_TESTING = "Bearer token";
    @MockBean
    UserRepo userRepo;

    @MockBean
    SellItemRepo sellItemRepo;

    @Autowired
    VendorService vendorService;

    @MockBean
    VendorOrdersRepo vendorOrdersRepo;

    @MockBean
    UserDetailsRepo userDetailsRepo;

    @MockBean
    CategoriesAcceptedRepo categoriesAcceptedRepo;

    @MockBean
    JwtUtil jwtUtil;

    User collector;
    User vendor;
    AcceptItemOnSaleDTO acceptItemOnSaleDTO;
    SellItemDTO sellItemDTO;
    SellItems sellItems;
    SellItems sellItems1;
    SellItems sellItems2;
    SellItems sellItems3;
    SellItems sellItems4;
    VendorOrders vendorOrders;

    VendorOrders vendorOrders1;
    UserDetails userDetails;
    AcceptItemOnSaleDTO acceptItemOnSaleDTO1;

    AcceptItemOnSaleDTO acceptItemOnSaleDTO2;

    @BeforeAll
    public void setUp()
    {
        collector = new User();
        collector.setFirstName("Abhinav");
        collector.setLastName("Singh");
        collector.setEmail("abhinav@gmail.com");
        collector.setCity("Bangalore");
        collector.setState("Karnataka");
        collector.setAddress1("BTM Layout");
        collector.setMobileNo("9898989898");
        collector.setPassword("123456");
        collector.setPinCode("560034");
        collector.setUid(collector.getUid());

        vendor= new User();
        vendor.setFirstName("Abhinav");
        vendor.setLastName("Singh");
        vendor.setEmail("abhinavsingh@gmail.com");
        vendor.setCity("Bangalore");
        vendor.setState("Karnataka");
        vendor.setAddress1("BTM Layout");
        vendor.setMobileNo("9898989898");
        vendor.setPassword("123456");
        vendor.setPinCode("560034");
        vendor.setUid(vendor.getUid());

        acceptItemOnSaleDTO =new AcceptItemOnSaleDTO();
        acceptItemOnSaleDTO.setId(1L);
        acceptItemOnSaleDTO.setDate("2022-08-22");
        acceptItemOnSaleDTO.setPrice("1000");
        acceptItemOnSaleDTO.setQuantity("12");


        acceptItemOnSaleDTO1 =new AcceptItemOnSaleDTO();
        acceptItemOnSaleDTO1.setId(1L);
        acceptItemOnSaleDTO1.setDate("2022-08-22");
        acceptItemOnSaleDTO1.setPrice("1000");
        acceptItemOnSaleDTO1.setQuantity("10000");

        acceptItemOnSaleDTO2 =new AcceptItemOnSaleDTO();
        acceptItemOnSaleDTO2.setId(1L);
        acceptItemOnSaleDTO2.setDate("2022-08-22");
        acceptItemOnSaleDTO2.setPrice("1000");
        acceptItemOnSaleDTO2.setQuantity("0");

        sellItemDTO = new SellItemDTO();
        sellItemDTO.setItemName("ABC");

        CategoriesAccepted category=new CategoriesAccepted();
        category.setCategoryAccepted("IT Appliances");

        sellItemDTO.setCategory(category);

        sellItemDTO.setStatus("Available");
        sellItemDTO.setPrice("1200");
        sellItemDTO.setId(1L);
        sellItemDTO.setQuantity("12");

        sellItems = new SellItems();
        sellItems.setId(sellItemDTO.getId());
        sellItems.setStatus(sellItemDTO.getStatus());
        sellItems.setItemName(sellItemDTO.getItemName());
        sellItems.setQuantity(sellItemDTO.getQuantity());
        sellItems.setPrice(sellItemDTO.getPrice());
        sellItems.setCollectorUid(collector.getUid());
        sellItems.setCategory(sellItemDTO.getCategory());
        sellItems.setAvailableQuantity(sellItemDTO.getQuantity());


        sellItems3 = new SellItems();
        sellItems3.setId(sellItemDTO.getId());
        sellItems3.setStatus(sellItemDTO.getStatus());
        sellItems3.setItemName(sellItemDTO.getItemName());
        sellItems3.setQuantity(sellItemDTO.getQuantity());
        sellItems3.setPrice(sellItemDTO.getPrice());
        sellItems3.setCollectorUid(collector.getUid());
        sellItems3.setCategory(sellItemDTO.getCategory());
        sellItems3.setAvailableQuantity("0");

        sellItems1 = new SellItems();
        sellItems1.setId(sellItemDTO.getId());
        sellItems1.setStatus(sellItemDTO.getStatus());
        sellItems1.setItemName(sellItemDTO.getItemName());
        sellItems1.setQuantity("0");
        sellItems1.setPrice(sellItemDTO.getPrice());
        sellItems1.setCollectorUid(collector.getUid());
        sellItems1.setCategory(sellItemDTO.getCategory());
        sellItems1.setAvailableQuantity("0");

        sellItems2 = new SellItems();
        sellItems2.setId(sellItemDTO.getId());
        sellItems2.setStatus(sellItemDTO.getStatus());
        sellItems2.setItemName(sellItemDTO.getItemName());
        sellItems2.setQuantity("0");
        sellItems2.setPrice(sellItemDTO.getPrice());
        sellItems2.setCollectorUid(collector.getUid());
        sellItems2.setCategory(sellItemDTO.getCategory());
        sellItems2.setAvailableQuantity("0");

        sellItems4 = new SellItems();
        sellItems4.setId(sellItemDTO.getId());
        sellItems4.setStatus(sellItemDTO.getStatus());
        sellItems4.setItemName(sellItemDTO.getItemName());
        sellItems4.setQuantity("0");
        sellItems4.setPrice(sellItemDTO.getPrice());
        sellItems4.setCategory(sellItemDTO.getCategory());
        sellItems4.setAvailableQuantity("0");

        vendorOrders = new VendorOrders();
        vendorOrders.setQuantity("9");
        vendorOrders.setStatus("completed");
        vendorOrders.setDate("2022-03-30");
        vendorOrders.setPrice("200");
        vendorOrders.setAddress("Bangalore");
        vendorOrders.setSellItems(sellItems);
        vendorOrders.setVendorUid(vendor.getUid());
        vendorOrders.setId(1L);

        vendorOrders1 = new VendorOrders();
        vendorOrders1.setQuantity("9");
        vendorOrders1.setStatus("completed");
        vendorOrders1.setDate("2022-03-30");
        vendorOrders1.setPrice("200");
        vendorOrders1.setAddress("Bangalore");
        vendorOrders1.setSellItems(sellItems4);
        vendorOrders1.setVendorUid(vendor.getUid());
        vendorOrders1.setId(1L);


        userDetails=new UserDetails();
        userDetails.setUser(vendor);
    }

    @Test
    void WHEN_VENDOR_PURCHASES_THE_ITEM_AVAILABLE_ON_SALE_SUCCESSFULLY() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(vendor.getEmail());

        when(userRepo.findUserByEmail(vendor.getEmail())).thenReturn(vendor);

        when(sellItemRepo.findByIdAndStatus(1L,"Available")).thenReturn(sellItems);

        when(userRepo.findUserByUid(sellItems.getCollectorUid())).thenReturn(collector);

        when(userDetailsRepo.findUserByUid(sellItems.getCollectorUid())).thenReturn(userDetails);

        ResponseMessage responseMessage = vendorService.purchaseItemsOnSale(acceptItemOnSaleDTO,request).getBody();

        assert responseMessage != null;
        VendorOrders vendorOrders= (VendorOrders) responseMessage.getData();

        assertEquals(sellItems.getCollectorUid(),vendorOrders.getSellItems().getCollectorUid());
    }

    @Test
    void WHEN_VENDOR_PURCHASES_THE_ITEM_AVAILABLE_ON_SALE_FAILURE_SCENARIO() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(vendor.getEmail());

        when(userRepo.findUserByEmail(vendor.getEmail())).thenReturn(vendor);

        when(sellItemRepo.findByIdAndStatus(1L,"Available")).thenReturn(sellItems1);

        when(userRepo.findUserByUid(sellItems.getCollectorUid())).thenReturn(collector);

        when(userDetailsRepo.findUserByUid(sellItems.getCollectorUid())).thenReturn(userDetails);

        ResponseMessage responseMessage = vendorService.purchaseItemsOnSale(acceptItemOnSaleDTO1,request).getBody();

        assert responseMessage != null;
        String vendorOrders= (String) responseMessage.getData();

        assertEquals(REDUCE_QUANTITY,vendorOrders);
    }

    @Test
    void WHEN_VENDOR_PURCHASES_THE_ITEM_AVAILABLE_ON_SALE_FAILURE_WHEN_ALL_ITEMS_ARE_SOLD_OUT() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(vendor.getEmail());

        when(userRepo.findUserByEmail(vendor.getEmail())).thenReturn(vendor);

        when(sellItemRepo.findByIdAndStatus(1L,"Available")).thenReturn(sellItems2);

        when(userRepo.findUserByUid(sellItems.getCollectorUid())).thenReturn(collector);

        when(userDetailsRepo.findUserByUid(sellItems.getCollectorUid())).thenReturn(userDetails);

        ResponseMessage responseMessage = vendorService.purchaseItemsOnSale(acceptItemOnSaleDTO2,request).getBody();

        assert responseMessage != null;
        SellItems vendorOrders= (SellItems) responseMessage.getData();

        assertEquals(sellItems2,vendorOrders);
    }


    @Test
    void WHEN_VENDOR_PURCHASES_THE_ITEM_AVAILABLE_ON_SALE_FAILURE_SCENARIO_BAD_REQUEST_EXCEPTION()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> vendorService.purchaseItemsOnSale(acceptItemOnSaleDTO,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_VENDOR_PURCHASES_THE_ITEM_AVAILABLE_ON_SALE_FAILURE_SCENARIO_INVALID_USER_EXCEPTION()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> vendorService.purchaseItemsOnSale(acceptItemOnSaleDTO,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }


    @Test
    void WHEN_VENDOR_VIEWS_ALL_THE_ITEMS_THAT_ARE_AVAILABLE_FOR_SALE_SUCCESS() {

        List<SellItems> sellItemsList = new ArrayList<>();
        sellItemsList.add(sellItems);
        when(sellItemRepo.findAllByStatus("Available", Pageable.ofSize(1))).thenReturn(new PageImpl<>(sellItemsList));
        ResponseMessageWithPagination responseMessage = vendorService.viewAllItemOnSale(1,1).getBody();
        assert responseMessage != null;
        List<SellItems> sellItemsList1 = (List<SellItems>) responseMessage.getData();
        assertEquals(sellItemsList.get(0).getCollectorUid(),sellItemsList1.get(0).getCollectorUid());
    }

    @Test
    void WHEN_VENDOR_VIEWS_ALL_THE_ITEMS_THAT_ARE_AVAILABLE_FOR_SALE_SUCCESS_Branching() {

        List<SellItems> sellItemsList = new ArrayList<>();
        sellItemsList.add(sellItems1);
        when(sellItemRepo.findAllByStatus("Available", Pageable.ofSize(1))).thenReturn(new PageImpl<>(sellItemsList));
        ResponseMessageWithPagination responseMessage = vendorService.viewAllItemOnSale(1,1).getBody();
        assert responseMessage != null;
        List<SellItems> sellItemsList1 = (List<SellItems>) responseMessage.getData();
        assertEquals(sellItemsList.get(0).getCollectorUid(),sellItemsList1.get(0).getCollectorUid());
    }

    @Test
    void WHEN_VENDOR_VIEWS_ALL_THE_ITEMS_THAT_ARE_AVAILABLE_FOR_SALE_FAILURE_NO_DATA_EXCEPTION()throws BadRequestException {
        when(sellItemRepo.findAllByStatus("Available",Pageable.ofSize(1))).thenReturn(new PageImpl<>(new ArrayList<>()));
        NoDataException exception = assertThrows(
                NoDataException.class, () -> vendorService.viewAllItemOnSale(1,1)
        );

        assertEquals(NO_ITEM_FOUND+ " on sale", exception.getMessage());

    }

    @Test
    void WHEN_VENDOR_VIEWS_ALL_THE_ITEMS_THAT_ARE_AVAILABLE_FOR_SALE_FAILURE_UPDATE_OUT_OF_STOCK_STATUS()throws BadRequestException {
        List<SellItems> sellItemsList = new ArrayList<>();
        sellItemsList.add(sellItems3);

        when(sellItemRepo.findAllByStatus("Available",Pageable.ofSize(1))).thenReturn(new PageImpl<>(sellItemsList));

        ResponseMessageWithPagination responseMessage = vendorService.viewAllItemOnSale(1,1).getBody();
        List<SellItems> sellItemsList1 = (List<SellItems>) responseMessage.getData();

        assertEquals(sellItemsList.get(0).getCollectorUid(),sellItemsList1.get(0).getCollectorUid());

    }

    @Test
    void WHEN_VENDOR_WANTS_TO_VIEW_ALL_PURCHASED_ITEMS_SUMMARY_SUCCESS() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(vendor.getEmail());
        when(userRepo.findUserByEmail(vendor.getEmail())).thenReturn((vendor));

        List<VendorOrders> vendorOrdersList =  new ArrayList<>();
        vendorOrdersList.add(vendorOrders);

        when(vendorOrdersRepo.findAllByVendorUid(vendor.getUid())).thenReturn(vendorOrdersList);

        ResponseMessage responseMessage = vendorService.orderSummary(request).getBody();
        List<VendorOrders> vendorOrdersList1= (List<VendorOrders>) responseMessage.getData();

        assertEquals(vendorOrdersList.get(0).getSellItems().getCollectorUid(),vendorOrdersList1.get(0).getSellItems().getCollectorUid());

    }

    @Test
    void WHEN_VENDOR_WANTS_TO_VIEW_ALL_PURCHASED_ITEMS_SUMMARY_FAILURE_CASE_NO_ORDERS_AVAILABLE() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(vendor.getEmail());

        when(userRepo.findUserByEmail(vendor.getEmail())).thenReturn((vendor));

        when(vendorOrdersRepo.findAllByVendorUid(vendor.getUid())).thenReturn(new ArrayList<>());
        ResponseMessage responseMessage2 = vendorService.orderSummary(request).getBody();
        assert responseMessage2 != null;
        assertEquals(NO_ORDERS, responseMessage2.getData());
    }

    @Test
    void WHEN_VENDOR_WANTS_TO_VIEW_COLLECTORS_PROFILE_IN_PURCHASED_ORDER_SUMMARY_SUCCESS() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());


        when(vendorOrdersRepo.findById(1L)).thenReturn(Optional.of(vendorOrders));
        when(userRepo.findUserByUid(vendorOrders.getSellItems().getCollectorUid())).thenReturn(collector);
        ResponseMessage responseMessage =vendorService.viewCollectorProfileInSummary(vendorOrders.getId(), request).getBody();
        assert responseMessage != null;
        User collector1=(User) responseMessage.getData();
        assertEquals(vendorOrders.getSellItems().getCollectorUid(),collector1.getUid());
    }

    @Test
    void WHEN_VENDOR_WANTS_TO_VIEW_COLLECTORS_PROFILE_IN_PURCHASED_ORDER_SUMMARY_FAILURE() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorOrdersRepo.findById(1L)).thenReturn(Optional.of(vendorOrders));

        when(userRepo.findUserByUid(vendorOrders.getSellItems().getCollectorUid())).thenReturn(null);
        ResponseMessage responseMessage2 =vendorService.viewCollectorProfileInSummary(vendorOrders.getId(), request).getBody();
        assert responseMessage2 != null;
        assertEquals(NO_COLLECTOR_FOUND, responseMessage2.getData());
    }

//    @Test
//    void WHEN_VENDOR_WANTS_TO_VIEW_COLLECTORS_PROFILE_IN_PURCHASED_ORDER_SUMMARY_FAILURE_TEST() {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader("EMAIL", vendor.getEmail());
//
//        when(vendorOrdersRepo.findById(1L)).thenReturn(null);
//
//        when(userRepo.findUserByUid(vendorOrders.getSellItems().getCollectorUid())).thenReturn(null);
//        ResponseMessage responseMessage2 =vendorService.viewCollectorProfileInSummary(vendorOrders.getId(), request).getBody();
//        assert responseMessage2 != null;
//        assertEquals(NO_COLLECTOR_FOUND, responseMessage2.getData());
//    }

    @Test
    void WHEN_VENDOR_WANTS_TO_VIEW_COLLECTORS_PROFILE_IN_PURCHASED_ORDER_SUMMARY_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> vendorService.viewCollectorProfileInSummary(null,request)
        );

        assertEquals(NO_COLLECTOR_FOUND, exception.getMessage());

    }
    @Test
    void WHEN_VENDOR_WANTS_TO_VIEW_COLLECTORS_PROFILE_IN_AVAILABLE_ITEMS_ON_SALE_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());


        when(sellItemRepo.findById(1L)).thenReturn(Optional.of(sellItems));
        when(userRepo.findUserByUid(sellItems.getCollectorUid())).thenReturn(collector);
        ResponseMessage responseMessage = vendorService.viewCollectorProfile(sellItems.getId(), request).getBody();
        assert responseMessage != null;
        User collector1=(User) responseMessage.getData();
        assertEquals(sellItems.getCollectorUid(),collector1.getUid());

    }

    @Test
    void WHEN_VENDOR_WANTS_TO_VIEW_COLLECTORS_PROFILE_IN_AVAILABLE_ITEMS_ON_SALE_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(sellItemRepo.findById(1L)).thenReturn(Optional.of(sellItems));

        when(userRepo.findUserByUid(sellItems.getCollectorUid())).thenReturn(null);
        ResponseMessage responseMessage2 =vendorService.viewCollectorProfile(sellItems.getId(), request).getBody();
        assert responseMessage2 != null;
        assertEquals(NO_COLLECTOR_FOUND, responseMessage2.getData());
    }

    @Test
    void WHEN_VENDOR_WANTS_TO_VIEW_COLLECTORS_PROFILE_IN_AVAILABLE_ITEMS_ON_SALE_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> vendorService.viewCollectorProfile(null,request)
        );

        assertEquals(NO_COLLECTOR_FOUND, exception.getMessage());

    }
    @Test
    void getAnalytics()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(vendor.getEmail());

        when(userRepo.findUserByEmail(vendor.getEmail())).thenReturn((vendor));
        List<User>listVendorInCity= new ArrayList<>();
        listVendorInCity.add(vendor);
        when(userRepo.findAllUsersByRoleAndCity(VENDOR,collector.getCity())).thenReturn(listVendorInCity);

        List<User>listOfVendorAll= new ArrayList<>();
        listOfVendorAll.add(vendor);
        listOfVendorAll.add(vendor);
        when(userRepo.findAll()).thenReturn(listOfVendorAll);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("vendorInCity",listVendorInCity.size());
        hashMap.put("allVendor",listOfVendorAll.size());


        List<User> collectorList = new ArrayList<>();
        collectorList.add(collector);
        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,vendor.getCity())).thenReturn(collectorList);

        List<User> allCollectorList = new ArrayList<>();
        allCollectorList.add(collector);
        when(userRepo.findAll()).thenReturn(allCollectorList);
        hashMap.put("collectorInCity",collectorList.size());
        hashMap.put("allCollector",allCollectorList.size());

        ResponseMessage responseMessage = vendorService.getAnalytics(request).getBody();
        HashMap<String,Integer> hashMap1 = (HashMap) responseMessage.getData();

        assertEquals(hashMap.get(VENDOR_IN_CITY),hashMap1.get(VENDOR_IN_CITY));
        assertEquals(hashMap.get(COLLECTOR_IN_CITY),hashMap1.get(COLLECTOR_IN_CITY));
    }
    @Test
    void getAnalytics_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> vendorService.getAnalytics(request)
        );

        assertEquals(EMAIL_CANNOT_BE_EMPTY, exception.getMessage());

    }

    @Test
    void getAnalytics_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> vendorService.getAnalytics(request)
        );

        assertEquals(USER_DOES_NOT_EXIST, exception.getMessage());

    }

    @Test
    void getAnalyticsV2()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(userRepo.findUserByEmail(vendor.getEmail())).thenReturn(vendor);

        List<User> collectorList = new ArrayList<>();
        collectorList.add(collector);

        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,vendor.getCity())).thenReturn(collectorList);

        List<SellItems> sellItemsListTemp = new ArrayList<>();
        sellItemsListTemp.add(sellItems);
        when(sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(TEMP, vendor.getUid())).thenReturn(sellItemsListTemp);

        ResponseMessage envelopeMessage=vendorService.getAnalyticsV2(request).getBody();
        HashMap<String,Integer>hashMap= (HashMap<String, Integer>) envelopeMessage.getData();

        assertEquals(0,hashMap.get(TEMP_COLLECTOR_SALE));
    }

    @Test
    void getAnalyticsV4(){
        List<User> collectorList = new ArrayList<>();
        collectorList.add(collector);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(vendor.getEmail());

        when(userRepo.findUserByEmail(vendor.getEmail())).thenReturn(vendor);


        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,vendor.getCity())).thenReturn(collectorList);
        when(userDetailsRepo.findUserByUid(collectorList.get(0).getUid())).thenReturn(userDetails);

        List<CategoriesAccepted> list1 = new ArrayList<>();
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted(TEMP);
        list1.add(categoriesAccepted);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, collector.getId())).thenReturn(list1);

        ResponseMessage responseMessage =vendorService.getAnalyticsV4(request).getBody();
        HashMap<String,Integer>hashMap= (HashMap<String, Integer>) responseMessage.getData();

        assertEquals(1,hashMap.get(TEMP_CITY));
    }
    @Test
    void getAnalyticsV3_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> vendorService.getAnalyticsV4(request)
        );

        assertEquals(EMAIL_CANNOT_BE_EMPTY, exception.getMessage());

    }

    @Test
    void getAnalyticsV3_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> vendorService.getAnalyticsV4(request)
        );

        assertEquals(USER_DOES_NOT_EXIST, exception.getMessage());

    }
}