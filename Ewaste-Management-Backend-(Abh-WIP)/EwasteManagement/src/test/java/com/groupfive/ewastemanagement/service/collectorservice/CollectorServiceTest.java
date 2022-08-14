package com.groupfive.ewastemanagement.service.collectorservice;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.*;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.helper.Util;
import com.groupfive.ewastemanagement.dto.request.EWasteDriveDTO;
import com.groupfive.ewastemanagement.dto.request.SellItemDTO;
import com.groupfive.ewastemanagement.repository.AllPendingRequestRepo;
import com.groupfive.ewastemanagement.repository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.EWasteDriveRepo;
import com.groupfive.ewastemanagement.repository.SellItemRepo;
import com.groupfive.ewastemanagement.repository.OrdersRepo;
import com.groupfive.ewastemanagement.repository.NotificationRepo;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.repository.VendorOrdersRepo;
import com.groupfive.ewastemanagement.service.CollectorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CollectorServiceTest {

    public static final String AUTHORIZATION_TEST_TOKEN = "Bearer token";
    @MockBean
    UserRepo userRepo;

    @MockBean
    CategoriesAcceptedRepo categoriesAcceptedRepo;

    @MockBean
    AllPendingRequestRepo allPendingRequestRepo;

    @MockBean
    OrdersRepo ordersRepo;

    @MockBean
    EWasteDriveRepo eWasteDriveRepo;

    @MockBean
    SellItemRepo sellItemRepo;

    @MockBean
    NotificationRepo notificationRepo;

    @MockBean
    VendorOrdersRepo vendorOrdersRepo;

    @MockBean
    JwtUtil jwtUtil;

    private final CollectorService collectorService;

    @Autowired
    public CollectorServiceTest(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    User collector;
    AllPendingRequest allPendingRequest;
    AllPendingRequest allPendingRequest1;
    EWasteDriveDTO eWasteDriveDTO;
    Orders orders;
    User customer;
    EWasteDrive eWasteDrive;
    EWasteDrive eWasteDrive1;
    EWasteDrive eWasteDriveExpired;
    SellItems sellItems;
    SellItems sellItemsFailure;
    SellItemDTO sellItemDTO;
    SellItemDTO sellItemDTOFailure;
    User vendor;
    VendorOrders vendorOrders;
    Util utilities;

    @BeforeAll
    public void setUp()
    {
        customer= new User();
        customer.setFirstName("Abhinav");
        customer.setLastName("Singh");
        customer.setEmail("abhinav@gmail.com");
        customer.setCity("Bangalore");
        customer.setState("Karnataka");
        customer.setAddress1("BTM Layout");
        customer.setMobileNo("9898989898");
        customer.setPassword("123456");
        customer.setPinCode("560034");
        customer.setUid(customer.getUid());
        Set<Role>roles=new HashSet<>();
        Role role=new Role();
        role.setName(CUSTOMER);
        roles.add(role);
        customer.setRoles(roles);

        collector=new User();
        collector.setFirstName("Abhinav");
        collector.setLastName("Singh Collector");
        collector.setEmail("abhinavsingh@gmail.com");
        collector.setCity("Bangalore");
        collector.setState("Karnataka");
        collector.setAddress1("BTM Layout");
        collector.setMobileNo("9898989898");
        collector.setPassword("123456");
        collector.setPinCode("560034");
        collector.setUid(collector.getUid());

        orders=new Orders();
        orders.setOrderUid("123");
        orders.setStatus(PENDING);

        CategoriesAccepted categories=new CategoriesAccepted();
        categories.setCategoryAccepted("IT Appliances");
        orders.setCategory(categories);

        orders.setCustomerUid(customer.getUid());
        orders.setScheduledDate("2022-02-21");
        orders.setScheduledTime("12:00");
        orders.setQuantity("4");
        orders.setItemName("Laptop");
        orders.setRequestType(PENDING);

        allPendingRequest= new AllPendingRequest();
        allPendingRequest.setOrderId(orders.getOrderUid());
        allPendingRequest.setCollectorUid(collector.getUid());
        allPendingRequest.setStatus(PENDING);

        CategoriesAccepted categoriesPendingRequest=new CategoriesAccepted();
        categoriesPendingRequest.setCategoryAccepted("IT Appliances");

        allPendingRequest.setCategory(categoriesPendingRequest);
        allPendingRequest.setQuantity("4");
        allPendingRequest.setRequestType("pickUp");
        allPendingRequest.setScheduleDate("2022-07-10");
        allPendingRequest.setScheduledTime("jhj");
        allPendingRequest.setItemName("Laptop");

        allPendingRequest1= new AllPendingRequest();
        allPendingRequest1.setOrderId("123");
        allPendingRequest1.setCollectorUid(collector.getUid());
        allPendingRequest1.setStatus("pending");

        CategoriesAccepted categoriesPendingRequest1=new CategoriesAccepted();
        categoriesPendingRequest1.setCategoryAccepted("IT Appliances");
        allPendingRequest1.setCategory(categoriesPendingRequest1);

        allPendingRequest1.setQuantity("4");
        allPendingRequest1.setRequestType("pickUp");
        allPendingRequest1.setScheduleDate("2022-02-21");
        allPendingRequest1.setScheduledTime("jhj");
        allPendingRequest1.setItemName("Laptop");

        eWasteDriveDTO = new EWasteDriveDTO();
        eWasteDriveDTO.setDriveName("Demo Drive");
        eWasteDriveDTO.setDescription("Demo Drive");
        eWasteDriveDTO.setOrganizerName(collector.getFirstName());

        CategoriesAccepted categoriesAccepted = new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("IT Appliances");

        Set<CategoriesAccepted> set=new HashSet<>();
        set.add(categoriesAccepted);

        eWasteDriveDTO.setCategoryAcceptedSet(set);
        eWasteDriveDTO.setDate("2022-07-10");
        eWasteDriveDTO.setTime("07:23");
        eWasteDriveDTO.setLocation("BLR");
        eWasteDriveDTO.setStatus(UPCOMING);

        //Drive Creation

        eWasteDrive = new EWasteDrive();
        eWasteDrive.setDriveName(eWasteDriveDTO.getDriveName());
        eWasteDrive.setDescription(eWasteDriveDTO.getDescription());
        eWasteDrive.setOrganizerName(eWasteDriveDTO.getOrganizerName());

        eWasteDrive.setEWasteCategoryAccepted(eWasteDriveDTO.getCategoryAcceptedSet());
        eWasteDrive.setDate(eWasteDriveDTO.getDate());
        eWasteDrive.setTime(eWasteDriveDTO.getTime());
        eWasteDrive.setLocation(eWasteDriveDTO.getLocation());
        eWasteDrive.setStatus(eWasteDriveDTO.getStatus());
        eWasteDrive.setCollectorEmail(collector.getEmail());


        eWasteDrive1 = new EWasteDrive();
        eWasteDrive1.setDriveName(eWasteDriveDTO.getDriveName());
        eWasteDrive1.setDescription(eWasteDriveDTO.getDescription());
        eWasteDrive1.setOrganizerName(eWasteDriveDTO.getOrganizerName());

        eWasteDrive1.setEWasteCategoryAccepted(eWasteDriveDTO.getCategoryAcceptedSet());
        eWasteDrive1.setDate("2022-07-30");
        eWasteDrive1.setTime(eWasteDriveDTO.getTime());
        eWasteDrive1.setLocation(eWasteDriveDTO.getLocation());
        eWasteDrive1.setStatus(eWasteDriveDTO.getStatus());
        eWasteDrive1.setCollectorEmail(collector.getEmail());


        eWasteDriveExpired= new EWasteDrive();
        eWasteDriveExpired.setDriveName(eWasteDriveDTO.getDriveName());
        eWasteDriveExpired.setDescription(eWasteDriveDTO.getDescription());
        eWasteDriveExpired.setOrganizerName(eWasteDriveDTO.getOrganizerName());

        eWasteDriveExpired.setEWasteCategoryAccepted(eWasteDriveDTO.getCategoryAcceptedSet());
        eWasteDriveExpired.setDate("2022-07-10");
        eWasteDriveExpired.setTime(eWasteDriveDTO.getTime());
        eWasteDriveExpired.setLocation(eWasteDriveDTO.getLocation());
        eWasteDriveExpired.setStatus(eWasteDriveDTO.getStatus());
        eWasteDriveExpired.setCollectorEmail(collector.getEmail());

        sellItemDTO = new SellItemDTO();
        sellItemDTO.setItemName("TV");
        sellItemDTO.setCategory(categoriesAccepted);
        sellItemDTO.setStatus(AVAILABLE);
        sellItemDTO.setPrice("1200");
        sellItemDTO.setId(1L);
        sellItemDTO.setQuantity("12");

        sellItems = new SellItems();
        sellItems.setId(sellItemDTO.getId());
        sellItems.setStatus(sellItemDTO.getStatus());
        sellItems.setItemName(sellItemDTO.getItemName());
        sellItems.setQuantity(sellItemDTO.getQuantity());
        sellItems.setPrice(sellItemDTO.getPrice());
        sellItems.setCategory(sellItemDTO.getCategory());
        sellItems.setAvailableQuantity(sellItemDTO.getQuantity());
        sellItems.setCollectorUid(collector.getUid());

        sellItemDTOFailure = new SellItemDTO();
        sellItemDTOFailure.setItemName("TV");
        sellItemDTOFailure.setCategory(categoriesAccepted);
        sellItemDTOFailure.setStatus(AVAILABLE);
        sellItemDTOFailure.setPrice("1200");
        sellItemDTOFailure.setId(1L);
        sellItemDTOFailure.setQuantity("0");

        sellItemsFailure = new SellItems();
        sellItemsFailure.setId(sellItemDTO.getId());
        sellItemsFailure.setStatus(sellItemDTO.getStatus());
        sellItemsFailure.setItemName(sellItemDTO.getItemName());
        sellItemsFailure.setQuantity("0");
        sellItemsFailure.setPrice(sellItemDTO.getPrice());
        sellItemsFailure.setCategory(sellItemDTO.getCategory());
        sellItemsFailure.setAvailableQuantity("0");
        sellItemsFailure.setCollectorUid(collector.getUid());

        vendor= new User();
        vendor.setFirstName("Abhinav");
        vendor.setLastName("Vendor");
        vendor.setEmail("abhinavvendor@gmail.com");
        vendor.setCity("Bangalore");
        vendor.setState("Karnataka");
        vendor.setAddress1("BTM Layout");
        vendor.setMobileNo("9898989898");
        vendor.setPassword("123456");
        vendor.setPinCode("560034");
        vendor.setUid(vendor.getUid());

        vendorOrders=new VendorOrders();
        vendorOrders.setSellItems(sellItems);
        vendorOrders.setQuantity(sellItemDTO.getQuantity());
        vendorOrders.setStatus(sellItemDTO.getStatus());
        vendorOrders.setPrice(sellItemDTO.getPrice());

        utilities = Mockito.mock(Util.class);
    }

    @Test
    void EXPIRE_THE_PENDING_REQUEST()
    {
        when(ordersRepo.findByOrderUid(allPendingRequest.getOrderId())).thenReturn(orders);
        when(ordersRepo.save(orders)).thenReturn(orders);
        when(allPendingRequestRepo.save(allPendingRequest)).thenReturn(allPendingRequest);

        collectorService.expirePendingRequest(allPendingRequest);

        assertEquals(5,2+3);
    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_VIEW_ALL_THE_PENDING_REQUEST_SUCCESS(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);

        List<AllPendingRequest> allPendingRequestList = new ArrayList<>();
        allPendingRequestList.add(allPendingRequest);
        allPendingRequestList.add(allPendingRequest1);

        when(allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(),"pending", Pageable.ofSize(1))).thenReturn(new PageImpl<>(allPendingRequestList));

//        PowerMocki.mockStatic(Utils.class);
        try (MockedStatic<Util> mockedStatic = Mockito.mockStatic(Util.class)) {
            mockedStatic.when(() -> Util.checkExpiryOfRequest("2022-01-20")).thenReturn(true);
        }

        when(ordersRepo.findByOrderUid(allPendingRequest1.getOrderId())).thenReturn(orders);
        ResponseMessageWithPagination responseMessage = collectorService.getPendingRequest(1,1,request).getBody();
        List<AllPendingRequest> allPendingRequestList2 = (List<AllPendingRequest>) responseMessage.getData();
        assertEquals(allPendingRequestList.get(0).getCollectorUid(),allPendingRequestList2.get(0).getCollectorUid());

        assertEquals("pending",allPendingRequestList2.get(1).getStatus());
    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_VIEW_ALL_THE_PENDING_REQUEST_BUT_THE_REQUEST_HAS_EXPIRED(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);

        List<AllPendingRequest> allPendingRequestList = new ArrayList<>();
        allPendingRequestList.add(allPendingRequest1);

        when(allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(),"pending", Pageable.ofSize(1))).thenReturn(new PageImpl<>(allPendingRequestList));
        when(ordersRepo.findByOrderUid(allPendingRequest1.getOrderId())).thenReturn(orders);
        ResponseMessageWithPagination responseMessage = collectorService.getPendingRequest(1,1,request).getBody();
        assert responseMessage != null;
        List<AllPendingRequest> allPendingRequestList2 = (List<AllPendingRequest>) responseMessage.getData();
        assertEquals(allPendingRequestList.get(0).getCollectorUid(),allPendingRequestList2.get(0).getCollectorUid());

        assertEquals("pending",allPendingRequestList2.get(0).getStatus());
    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_VIEW_ALL_THE_PENDING_REQUEST_FAILURE(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);

        List<AllPendingRequest> allPendingRequestList1 = new ArrayList<>();
        when(allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(),"pending", Pageable.ofSize(1))).thenReturn(new PageImpl<>(allPendingRequestList1));
        ResponseMessageWithPagination responseMessage2 = collectorService.getPendingRequest(1,1,request).getBody();
        assert responseMessage2 != null;
        assertEquals("No Request Pending", responseMessage2.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_VIEW_ALL_THE_PENDING_REQUEST_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.getPendingRequest(1,1,request)
        );

       assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_VIEW_ALL_THE_PENDING_REQUEST_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.getPendingRequest(1,1,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_VIEW_ALL_HIS_REQUEST_SUMMARY_SUCCESS()
    {
        orders.setCollectorUid(collector.getUid());
        orders.setCustomerUid(customer.getUid());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);
        List<Orders> ordersList1 = new ArrayList<>();
        ordersList1.add(orders);

        when(ordersRepo.findByCollectorUidAndStatus(collector.getUid(),"Scheduled",Pageable.ofSize(1))).thenReturn(new PageImpl<>(ordersList1));
        ResponseMessageWithPagination responseMessage = collectorService.getRequestSummary(1,1,request).getBody();
        assert responseMessage != null;
        List<Orders> ordersList = (List<Orders>) responseMessage.getData();

        assertEquals(ordersList1.get(0).getCollectorUid(),ordersList.get(0).getCollectorUid());
    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_VIEW_ALL_HIS_REQUEST_SUMMARY_FAILURE(){

        orders.setCollectorUid(collector.getUid());
        orders.setCustomerUid(customer.getUid());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);

        List<Orders> ordersList2 = new ArrayList<>();
        when(ordersRepo.findByCollectorUidAndStatus(collector.getUid(),"Scheduled",Pageable.ofSize(1))).thenReturn(new PageImpl<>(ordersList2));
        ResponseMessageWithPagination responseMessage1 = collectorService.getRequestSummary(1,1,request).getBody();

        assert responseMessage1 != null;
        assertEquals("No orders", responseMessage1.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_VIEW_ALL_HIS_REQUEST_SUMMARY_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.getRequestSummary(1,1,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_VIEW_ALL_HIS_REQUEST_SUMMARY_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.getRequestSummary(1,1, request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_ACCEPTS_A_PENDING_REQUEST_Success()
    {
        orders.setCollectorUid(collector.getUid());
        orders.setCustomerUid(customer.getUid());
        orders.setOrderUid(UUID.randomUUID().toString());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));
        when(ordersRepo.findByOrderUid(orders.getOrderUid())).thenReturn(orders);
        when(userRepo.findUserByUid(orders.getCustomerUid())).thenReturn(customer);
        List<AllPendingRequest>listPendingRequests=new ArrayList<>();
        listPendingRequests.add(allPendingRequest);
        when(allPendingRequestRepo.findByOrderId(orders.getOrderUid())).thenReturn(listPendingRequests);

        ResponseMessage responseMessage = collectorService.acceptPendingRequest(orders.getOrderUid(),request).getBody();
        assert responseMessage != null;
        Orders  orders1 = (Orders) responseMessage.getData();
        assertEquals(orders.getCollectorUid(),orders1.getCollectorUid());
    }

    @Test
    void WHEN_COLLECTOR_ACCEPTS_A_PENDING_REQUEST_Failure()
    {
        orders.setCollectorUid(collector.getUid());
        orders.setCustomerUid(customer.getUid());
        orders.setOrderUid(UUID.randomUUID().toString());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));
        when(ordersRepo.findByOrderUid(orders.getOrderUid())).thenReturn(orders);
        when(userRepo.findUserByUid(orders.getCustomerUid())).thenReturn(customer);

        when(ordersRepo.findByOrderUid(orders.getOrderUid())).thenReturn(null);
        ResponseMessage responseMessage1 = collectorService.acceptPendingRequest(orders.getOrderUid(),request).getBody();
        assert responseMessage1 != null;
        assertEquals(NO_SUCH_ORDER_EXIST, responseMessage1.getData());

    }

    @Test
    void WHEN_COLLECTOR_ACCEPTS_A_PENDING_REQUEST_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.acceptPendingRequest(null,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_ACCEPTS_A_PENDING_REQUEST_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test1@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.acceptPendingRequest("null",request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_ORGANIZE_EWASTE_DRIVE_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));
        List<User> customerList = new ArrayList<>();
        customerList.add(customer);
        when(userRepo.findAllUsersByRoleAndCity(CUSTOMER,collector.getCity())).thenReturn(customerList);

        ResponseMessage responseMessage = collectorService.organizeDrive(eWasteDriveDTO,request).getBody();
        assert responseMessage != null;
        EWasteDrive eWasteDrive1 = (EWasteDrive) responseMessage.getData();

        assertEquals(eWasteDrive.getCollectorEmail(),eWasteDrive1.getCollectorEmail());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_ORGANIZE_EWASTE_DRIVE_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));
        List<User> customerList = new ArrayList<>();
        customerList.add(customer);
        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,collector.getCity())).thenReturn(customerList);

        ResponseMessage responseMessage1 = collectorService.organizeDrive(null,request).getBody();
        assert responseMessage1 != null;
        assertEquals("No data provided", responseMessage1.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_ORGANIZE_EWASTE_DRIVE_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.organizeDrive(eWasteDriveDTO,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_ORGANIZE_EWASTE_DRIVE_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.organizeDrive(eWasteDriveDTO,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }
    @Test
    void WHEN_COLLECTOR_WANT_TO_SELL_EWASTE_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());
        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);

        ResponseMessage responseMessage = collectorService.sellItem(sellItemDTO,request).getBody();
        assert responseMessage != null;
        SellItems sellItems1 = (SellItems) responseMessage.getData();

        assertEquals(sellItems.getCollectorUid(),sellItems1.getCollectorUid());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_SELL_EWASTE_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);

        ResponseMessage responseMessage1 = collectorService.sellItem(null,request).getBody();
        assert responseMessage1 != null;
        assertEquals("Enter all Details", responseMessage1.getData());
    }

    @Test
    void WWHEN_COLLECTOR_WANT_TO_SELL_EWASTE_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.sellItem(sellItemDTO,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_SELL_EWASTE_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.sellItem(sellItemDTO,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_EWASTE_DRIVE_IS_EXPIRED_Success(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);
        List<EWasteDrive> eWasteDriveList = new ArrayList<>();
        eWasteDriveList.add(eWasteDrive);

        try (MockedStatic<Util> mockedStatic = Mockito.mockStatic(Util.class)) {
            mockedStatic.when(() -> Util.checkExpiryOfRequest("2022-01-20")).thenReturn(true);
        }

        when(eWasteDriveRepo.getAllByCollectorEmail(collector.getEmail(),Pageable.ofSize(1))).thenReturn(new PageImpl<>(eWasteDriveList));
        ResponseMessageWithPagination responseMessage = collectorService.viewMyDrive(1,1,request).getBody();
        assert responseMessage != null;
        List<EWasteDrive> eWasteDriveList1 = (List<EWasteDrive>) responseMessage.getData();
        assertEquals(eWasteDrive.getCollectorEmail(),eWasteDriveList1.get(0).getCollectorEmail());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_ALL_HIS_SCHEDULED_EWASTE_DRIVES_WHEN_DRIVE_Success(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);
        List<EWasteDrive> eWasteDriveList = new ArrayList<>();
        eWasteDriveList.add(eWasteDrive1);

        try (MockedStatic<Util> mockedStatic = Mockito.mockStatic(Util.class)) {
            mockedStatic.when(() -> Util.checkExpiryOfRequest("2022-01-20")).thenReturn(true);
        }

        when(eWasteDriveRepo.getAllByCollectorEmail(collector.getEmail(),Pageable.ofSize(1))).thenReturn(new PageImpl<>(eWasteDriveList));
        ResponseMessageWithPagination responseMessage = collectorService.viewMyDrive(1,1,request).getBody();
        assert responseMessage != null;
        List<EWasteDrive> eWasteDriveList1 = (List<EWasteDrive>) responseMessage.getData();
        assertEquals(eWasteDrive.getCollectorEmail(),eWasteDriveList1.get(0).getCollectorEmail());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_ALL_HIS_SCHEDULED_EWASTE_DRIVES_Failure(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn(collector);

        List<EWasteDrive> eWasteDriveList2 = new ArrayList<>();
        when(eWasteDriveRepo.getAllByCollectorEmail(collector.getEmail(),Pageable.ofSize(1))).thenReturn(new PageImpl<>(eWasteDriveList2));
        ResponseMessageWithPagination responseMessage1 = collectorService.viewMyDrive(1,1,request).getBody();
        assert responseMessage1 != null;
        assertEquals(NO_DRIVES_AVAILABLE, responseMessage1.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_ALL_HIS_SCHEDULED_EWASTE_DRIVES_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.viewMyDrive(1,1,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_ALL_HIS_SCHEDULED_EWASTE_DRIVES_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.viewMyDrive(1,1,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_SUMMARY_OF_ALL_ITEMS_AVAILABLE_ON_SALE_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<SellItems> sellItemsList = new ArrayList<>();
        sellItemsList.add(sellItems);
        when(sellItemRepo.findAllByCollectorUidAndStatus(collector.getUid(),AVAILABLE)).thenReturn(sellItemsList);

        ResponseMessage responseMessage = collectorService.sellItemAvailableSummary(request).getBody();
        assert responseMessage != null;
        List<SellItems> itemsList = (List<SellItems>) responseMessage.getData();
        assertEquals(sellItemsList.get(0).getCollectorUid(), itemsList.get(0).getCollectorUid());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_SUMMARY_OF_ALL_ITEMS_AVAILABLE_ON_SALE_Failure() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));
        List<SellItems> sellItemsList1 = new ArrayList<>();
        when(sellItemRepo.findAllByCollectorUidAndStatus(collector.getUid(),AVAILABLE)).thenReturn(sellItemsList1);
        ResponseMessage responseMessage1 = collectorService.sellItemAvailableSummary(request).getBody();

        assert responseMessage1 != null;
        assertEquals("No items in Sale", responseMessage1.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_SUMMARY_OF_ALL_ITEMS_AVAILABLE_ON_SALE_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.sellItemAvailableSummary(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_SUMMARY_OF_ALL_ITEMS_AVAILABLE_ON_SALE_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.sellItemAvailableSummary( request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }


    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_SUMMARY_OF_ALL_SOLD_ITEMS_ON_SALE_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<VendorOrders>vendorOrdersList=new ArrayList<>();
        vendorOrdersList.add(vendorOrders);
        when(vendorOrdersRepo.findAllByCollectorUid(collector.getUid())).thenReturn(vendorOrdersList);

        ResponseMessage responseMessage = collectorService.sellItemSoldSummary(request).getBody();
        assert responseMessage != null;
        List<VendorOrders> itemsList = (List<VendorOrders>) responseMessage.getData();
        assertEquals(vendorOrdersList.get(0).getSellItems().getCollectorUid(), itemsList.get(0).getSellItems().getCollectorUid());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_SUMMARY_OF_ALL_SOLD_ITEMS_ON_SALE_Failure() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<VendorOrders>vendorOrdersList=new ArrayList<>();
        when(vendorOrdersRepo.findAllByCollectorUid(collector.getUid())).thenReturn(vendorOrdersList);

        ResponseMessage responseMessage = collectorService.sellItemSoldSummary(request).getBody();

        assert responseMessage != null;
        assertEquals("No items in Sale", responseMessage.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_SUMMARY_OF_ALL_SOLD_ITEMS_ON_SALE_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.sellItemSoldSummary(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_SUMMARY_OF_ALL_SOLD_ITEMS_ON_SALE_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.sellItemSoldSummary( request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_CUSTOMER_PROFILE_Success() {
        when(userRepo.findUserByUid(customer.getUid())).thenReturn(customer);
        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        ResponseMessage responseMessage = collectorService.viewCustomerProfile(customer.getUid()).getBody();
        assert responseMessage != null;
        User customer1=(User) responseMessage.getData();
        assertEquals(customer.getUid(),customer1.getUid());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_CUSTOMER_PROFILE_Failure() {
        when(userRepo.findUserByUid(customer.getUid())).thenReturn(null);
        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        ResponseMessage responseMessage1 = collectorService.viewCustomerProfile(customer.getUid()).getBody();
        assert responseMessage1 != null;
        assertEquals("Profile Not Found", responseMessage1.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_HIS_NOTIFICATIONS_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        Notification customerNotification=new Notification();
        customerNotification.setCustomerUid(collector.getUid());
        customerNotification.setRole("Collector");
        customerNotification.setMessage("TEST Message");
        customerNotification.setStatus(false);

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(customerNotification);
        when(notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid())).thenReturn(notificationList);

        ResponseMessage responseMessage = collectorService.viewNotification(request).getBody();
        assert responseMessage != null;
        List<Notification>customerNotification1= (List<Notification>) responseMessage.getData();

        assertEquals(notificationList.get(0).getCollectorUid(),customerNotification1.get(0).getCollectorUid());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_HIS_NOTIFICATIONS_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(EMAIL, collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<Notification> notificationList = new ArrayList<>();
        when(notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid())).thenReturn(notificationList);

        ResponseMessage responseMessage = collectorService.viewNotification(request).getBody();

        assert responseMessage != null;
        assertEquals(NO_NEW_NOTIFICATION, responseMessage.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_HIS_NOTIFICATIONS_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.viewNotification(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_VIEW_HIS_NOTIFICATIONS_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.viewNotification( request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_READ_HIS_NOTIFICATIONS_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(EMAIL, collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        Notification customerNotification=new Notification();
        customerNotification.setCustomerUid(collector.getUid());
        customerNotification.setRole("Collector");
        customerNotification.setMessage("TEST Message");
        customerNotification.setStatus(false);

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(customerNotification);
        when(notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid())).thenReturn(notificationList);

        ResponseMessage responseMessage = collectorService.readNotification(request).getBody();
        assert responseMessage != null;
        List<Notification>customerNotification1= (List<Notification>) responseMessage.getData();

        assertEquals(notificationList.get(0).getCollectorUid(),customerNotification1.get(0).getCollectorUid());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_READ_HIS_NOTIFICATIONS_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(EMAIL, collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<Notification> notificationList = new ArrayList<>();
        when(notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid())).thenReturn(notificationList);

        ResponseMessage responseMessage = collectorService.readNotification(request).getBody();

        assert responseMessage != null;
        assertEquals(NO_NEW_NOTIFICATION, responseMessage.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_READ_HIS_NOTIFICATIONS_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.readNotification(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_READ_HIS_NOTIFICATIONS_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.readNotification( request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }
    @Test
    void WHEN_COLLECTOR_WANT_TO_EDIT_HIS_SCHEDULED_DRIVE_STATUS_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(EMAIL, collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        when(eWasteDriveRepo.findByCollectorEmailAndId(collector.getEmail(),eWasteDrive.getId())).thenReturn(eWasteDrive);
        ResponseMessage responseMessage = collectorService.editDriveSummary(eWasteDrive.getId(),"upcoming",request).getBody();
        assert responseMessage != null;
        EWasteDrive eWasteDrive1= (EWasteDrive) responseMessage.getData();
        assertEquals(eWasteDrive.getCollectorEmail(),eWasteDrive1.getCollectorEmail());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_EDIT_HIS_SCHEDULED_DRIVE_STATUS_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(EMAIL, collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        when(eWasteDriveRepo.findByCollectorEmailAndId(collector.getEmail(),eWasteDrive.getId())).thenReturn(null);
        ResponseMessage responseMessage1 = collectorService.editDriveSummary(eWasteDrive.getId(),"upcoming",request).getBody();
        assert responseMessage1 != null;
        assertEquals(DRIVE_NOT_FOUND, responseMessage1.getData());
    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_EDIT_HIS_SCHEDULED_DRIVE_STATUS_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.editDriveSummary(1L,COMPLETED,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANT_TO_EDIT_HIS_SCHEDULED_DRIVE_STATUS_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.editDriveSummary(1L,COMPLETED,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void getAnalyticsV1()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(EMAIL, collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());
        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<EWasteDrive>listEWasteDriveInCity= new ArrayList<>();
        listEWasteDriveInCity.add(eWasteDrive);
        listEWasteDriveInCity.add(eWasteDrive);
        when(eWasteDriveRepo.findAllByCity(collector.getCity())).thenReturn(listEWasteDriveInCity);

        List<EWasteDrive>listOfEWasteDrive= new ArrayList<>();
        listOfEWasteDrive.add(eWasteDrive);
        when(eWasteDriveRepo.getAllEWasteAnalyticsByCollectorEmail(collector.getEmail())).thenReturn(listOfEWasteDrive);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put(E_WASTE_DRIVE_COLLECTOR,listOfEWasteDrive.size());
        hashMap.put(E_WASTE_DRIVE_CITY,listEWasteDriveInCity.size());

        ResponseMessage responseMessage = collectorService.getAnalytics(request).getBody();
        assert responseMessage != null;
        HashMap<String,Integer> hashMap1 = (HashMap<String,Integer>) responseMessage.getData();

        assertEquals(hashMap.get(E_WASTE_DRIVE_CITY),hashMap1.get(E_WASTE_DRIVE_CITY));

    }

    @Test
    void getAnalyticsV1_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.getAnalytics(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void getAnalyticsV1_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.getAnalytics(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void getAnalyticsV2()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<User> collectorList = new ArrayList<>();
        collectorList.add(collector);
        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,collector.getCity())).thenReturn(collectorList);

        List<Orders> listTempCollected =new ArrayList<>();
        listTempCollected.add(orders);
        when(ordersRepo.findByCategoryAndCollectorUid("Temp", collectorList.get(0).getUid())).thenReturn(listTempCollected);

        List<Orders> listLapmsCollected =new ArrayList<>();
        listLapmsCollected.add(orders);
        when(ordersRepo.findByCategoryAndCollectorUid("Lapms", collectorList.get(0).getUid())).thenReturn(listLapmsCollected);

        List<Orders> listLargeEqipCollected =new ArrayList<>();
        listLargeEqipCollected.add(orders);
        when(ordersRepo.findByCategoryAndCollectorUid("LargeEqip", collectorList.get(0).getUid())).thenReturn(listLargeEqipCollected);

        List<Orders> listSmallEquipCollected =new ArrayList<>();
        listSmallEquipCollected.add(orders);
        when(ordersRepo.findByCategoryAndCollectorUid("SmallEquip", collectorList.get(0).getUid())).thenReturn(listSmallEquipCollected);

        List<Orders> listSmallITCollected =new ArrayList<>();
        listSmallITCollected.add(orders);
        when(ordersRepo.findByCategoryAndCollectorUid("SmallIT", collectorList.get(0).getUid())).thenReturn(listSmallITCollected);

        List<Orders> listScreensCollected =new ArrayList<>();
        listScreensCollected.add(orders);
        when(ordersRepo.findByCategoryAndCollectorUid("Screens", collectorList.get(0).getUid())).thenReturn(listScreensCollected);

        HashMap<String,Integer>hashMap=new HashMap<>();
        hashMap.put(TEMP_COLLECTED,listTempCollected.size());
        hashMap.put(LAMPS_COLLECTED,listLapmsCollected.size());
        hashMap.put(LARGE_EQUIP_COLLECTED,listLargeEqipCollected.size());
        hashMap.put(SMALL_EQUIP_COLLECTED,listSmallEquipCollected.size());
        hashMap.put(SMALL_IT_COLLECTED,listSmallITCollected.size());
        hashMap.put(SCREENS_COLLECTED,listScreensCollected.size());

        ResponseMessage responseMessage = collectorService.getAnalyticsV2(request).getBody();
        assert responseMessage != null;
        HashMap<String,Integer>hm= (HashMap<String, Integer>) responseMessage.getData();

        assertEquals(hashMap.get(TEMP_COLLECTED),hm.get(TEMP_COLLECTED));
    }

    @Test
    void getAnalyticsV2_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.getAnalyticsV2(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void getAnalyticsV2_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.getAnalyticsV2(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }
    @Test
    void getAnalyticsV3()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<User>listVendorInCity= new ArrayList<>();
        listVendorInCity.add(vendor);
        when(userRepo.findAllUsersByRoleAndCity(VENDOR,collector.getCity())).thenReturn(listVendorInCity);

        List<User>listOfVendorAll= new ArrayList<>();
        listOfVendorAll.add(vendor);
        listOfVendorAll.add(vendor);
        when(userRepo.findAllUsersByRole(VENDOR)).thenReturn(listOfVendorAll);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("vendorCity",listVendorInCity.size());
        hashMap.put("vendorAllCity",listOfVendorAll.size());

        ResponseMessage responseMessage = collectorService.getAnalyticsV4(request).getBody();
        assert responseMessage != null;
        HashMap<String,Integer> hashMap1 = (HashMap<String,Integer>) responseMessage.getData();

        assertEquals(hashMap.get("vendorCity"),hashMap1.get("vendorCity"));

    }
    @Test
    void getAnalyticsV3_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.getAnalyticsV4(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void getAnalyticsV3_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.getAnalyticsV4(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void getAnalyticsV4()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<User>listCustomerInCity= new ArrayList<>();
        listCustomerInCity.add(customer);
        when(userRepo.findAllUsersByRoleAndCity(CUSTOMER,collector.getCity())).thenReturn(listCustomerInCity);

        List<User>listCustomerAll= new ArrayList<>();
        listCustomerAll.add(customer);
        listCustomerAll.add(customer);
        when(userRepo.findAllUsersByRole(CUSTOMER)).thenReturn(listCustomerAll);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("customerCity",listCustomerInCity.size());
        hashMap.put("customerAllCity",listCustomerAll.size());

        ResponseMessage responseMessage = collectorService.getAnalyticsV5(request).getBody();
        HashMap<String,Integer> hashMap1 = (HashMap<String,Integer>) responseMessage.getData();

        assertEquals(hashMap.get("customerCity"),hashMap1.get("customerCity"));
    }

    @Test
    void getAnalyticsV4_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.getAnalyticsV5(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void getAnalyticsV4_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.getAnalyticsV5(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_EDIT_ITEMS_AVAILABLE_ON_SALE_SUCCESS(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());


        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        when(sellItemRepo.findById(sellItemDTO.getId())).thenReturn(Optional.ofNullable(sellItems));

        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("IT Appliances");

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(sellItemDTO.getCategory().getCategoryAccepted())).thenReturn(categoriesAccepted);

        assertEquals(sellItems, Objects.requireNonNull(collectorService.editSellItemAvailableSummary(request, sellItemDTO).getBody()).getData());

    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_EDIT_ITEMS_AVAILABLE_ON_SALE_AND_TotalQuantityIsZero(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());


        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        when(sellItemRepo.findById(sellItemDTOFailure.getId())).thenReturn(Optional.ofNullable(sellItemsFailure));

        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("IT Appliances");

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(sellItemDTOFailure.getCategory().getCategoryAccepted())).thenReturn(categoriesAccepted);

        assertEquals("Value can not be less than zero", Objects.requireNonNull(collectorService.editSellItemAvailableSummary(request, sellItemDTOFailure).getBody()).getData());

    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_EDIT_ITEMS_AVAILABLE_ON_SALE_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.editSellItemAvailableSummary(request, sellItemDTO)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_COLLECTOR_WANTS_TO_EDIT_ITEMS_AVAILABLE_ON_SALE_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);
        when(sellItemRepo.findById(sellItemDTO.getId())).thenReturn(null);


        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.editSellItemAvailableSummary(request, sellItemDTO)
        );

        assertEquals(USER_DOES_NOT_EXIST, exception.getMessage());

    }

    @Test
    void getAnalyticsV5()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TEST_TOKEN);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(collector.getEmail());

        HashMap<String,Integer>hashMap=new HashMap<>();
        hashMap.put(SMALL_EQUIP,1200);

        when(userRepo.findUserByEmail(collector.getEmail())).thenReturn((collector));

        List<VendorOrders>list_vendorOrders=new ArrayList<>();
        list_vendorOrders.add(vendorOrders);
        when(vendorOrdersRepo.findOrdersByCollectorUidAndCategory(collector.getUid(), SMALL_EQUIP)).thenReturn(list_vendorOrders);
        when(vendorOrdersRepo.findOrdersByCollectorUidAndCategory(collector.getUid(), TEMP)).thenReturn(list_vendorOrders);
        when(vendorOrdersRepo.findOrdersByCollectorUidAndCategory(collector.getUid(), LAMPS)).thenReturn(list_vendorOrders);
        when(vendorOrdersRepo.findOrdersByCollectorUidAndCategory(collector.getUid(), LARGE_EQUIP)).thenReturn(list_vendorOrders);
        when(vendorOrdersRepo.findOrdersByCollectorUidAndCategory(collector.getUid(), SMALL_IT)).thenReturn(list_vendorOrders);
        when(vendorOrdersRepo.findOrdersByCollectorUidAndCategory(collector.getUid(), SCREENS)).thenReturn(list_vendorOrders);




        HashMap<String,Integer>hashMap1= (HashMap<String, Integer>) Objects.requireNonNull(collectorService.getAnalyticsV6(request).getBody()).getData();

        assertEquals( hashMap.get(SMALL_EQUIP),hashMap1.get(SMALL_EQUIP));

    }

    @Test
    void getAnalyticsV5_BadRequestException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> collectorService.getAnalyticsV6(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void getAnalyticsV5_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> collectorService.getAnalyticsV6(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }


}
