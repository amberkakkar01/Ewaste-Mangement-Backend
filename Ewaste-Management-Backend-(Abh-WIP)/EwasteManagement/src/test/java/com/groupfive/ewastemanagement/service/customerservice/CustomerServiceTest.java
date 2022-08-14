package com.groupfive.ewastemanagement.service.customerservice;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.EWasteDrive;
import com.groupfive.ewastemanagement.entity.UserDetails;
import com.groupfive.ewastemanagement.entity.Orders;
import com.groupfive.ewastemanagement.entity.Notification;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.helper.Util;
import com.groupfive.ewastemanagement.dto.request.RequestDTO;
import com.groupfive.ewastemanagement.repository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.EWasteDriveRepo;
import com.groupfive.ewastemanagement.repository.UserDetailsRepo;
import com.groupfive.ewastemanagement.repository.OrdersRepo;
import com.groupfive.ewastemanagement.repository.NotificationRepo;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.service.CustomerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerServiceTest {
    public static final String AUTHORIZATION_TOKEN_TESTING = "Bearer token";
    @Autowired
    CustomerService customerService;

    @MockBean
    UserRepo userRepo;

    @MockBean
    NotificationRepo notificationRepo;

    @MockBean
    OrdersRepo ordersRepo;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    CategoriesAcceptedRepo categoriesAcceptedRepo;

    @MockBean
    EWasteDriveRepo eWasteDriveRepo;

    @MockBean
    UserDetailsRepo userDetailsRepo;

    User customer;
    User collector;
    RequestDTO requestDTO;
    Orders orders;
    EWasteDrive eWasteDrive;
    EWasteDrive eWasteDrive1;
    UserDetails userDetails;

    @BeforeAll
    public void setUp()
    {
        CategoriesAccepted category=new CategoriesAccepted();
        category.setCategoryAccepted("Laptop");

        customer = new User();
        customer.setFirstName("Abhinav");
        customer.setLastName("Singh");
        customer.setEmail("abhinav@dmail.com");
        customer.setCity("Bangalore");
        customer.setState("Karnataka");
        customer.setAddress1("BTM LAYOUT");
        customer.setMobileNo("9875464777");
        customer.setPassword("123456");
        customer.setPinCode("560034");
        customer.setUid(customer.getUid());

        collector = new User();
        collector.setFirstName("Abhinav");
        collector.setLastName("Singh");
        collector.setEmail("abhinavsingh@dmail.com");
        collector.setCity("Bangalore");
        collector.setState("Karnataka");
        collector.setAddress1("BTM LAYOUT");
        collector.setMobileNo("8987454879");
        collector.setPassword("123456");
        collector.setPinCode("560034");

        userDetails=new UserDetails();
        userDetails.setUser(collector);
        Set<CategoriesAccepted>set=new HashSet<>();
        set.add(category);
        userDetails.setCategoriesAccepted(set);
        collector.setUid(collector.getUid());

        requestDTO = new RequestDTO();
        requestDTO.setRequestType(Constants.PICK_UP);
        requestDTO.setQuantity("20");
        requestDTO.setCategory(category);
        requestDTO.setItemName("Laptop");
        requestDTO.setScheduledDate("2022-03-19");
        requestDTO.setScheduledTime("14:13");
        requestDTO.setCollectorUid(collector.getUid());

        orders = new Orders();
        orders.setCategory(requestDTO.getCategory());
        orders.setQuantity(requestDTO.getQuantity());
        orders.setRequestType(Constants.PICK_UP);
        orders.setCustomerUid(customer.getUid());
        orders.setCollectorUid(collector.getUid());
        orders.setStatus(Constants.PENDING);
        orders.setOrderUid(orders.getOrderUid());
        orders.setItemName(requestDTO.getItemName());
        orders.setScheduledDate(requestDTO.getScheduledDate());
        orders.setScheduledTime(requestDTO.getScheduledTime());


        eWasteDrive = new EWasteDrive();
        eWasteDrive.setDriveName("XYZ");
        eWasteDrive.setDescription("yyyy");
        eWasteDrive.setOrganizerName("KOOK");
        eWasteDrive.setEWasteCategoryAccepted(set);
        eWasteDrive.setDate("2022-07-10");
        eWasteDrive.setTime("07:23");
        eWasteDrive.setLocation(collector.getCity());
        eWasteDrive.setCity("London");
        eWasteDrive.setStatus("Upcoming");
        eWasteDrive.setCollectorEmail(collector.getEmail());

        eWasteDrive1 = new EWasteDrive();
        eWasteDrive1.setDriveName("XYZ");
        eWasteDrive1.setDescription("yyyy");
        eWasteDrive1.setOrganizerName("KOOK");
        eWasteDrive1.setEWasteCategoryAccepted(set);
        eWasteDrive1.setDate("2022-07-30");
        eWasteDrive1.setTime("07:23");
        eWasteDrive1.setLocation(collector.getCity());
        eWasteDrive1.setCity("London");
        eWasteDrive1.setStatus("Upcoming");
        eWasteDrive1.setCollectorEmail(collector.getEmail());
    }
    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_ALL_HIS_ORDERS_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());


        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<Orders>list=new ArrayList<>();
        list.add(orders);

        when(ordersRepo.findAllByCustomerUid(customer.getUid(), Pageable.ofSize(1))).thenReturn(new PageImpl<>(list));

        ResponseMessageWithPagination responseMessage = customerService.getAllOrders(1,1,request).getBody();
        assert responseMessage != null;
        List<Orders>ordersList= (List<Orders>) responseMessage.getData();
        assertEquals(orders.getId(),ordersList.get(0).getId());

    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_ALL_HIS_ORDERS_Failure() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());


        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<Orders> list1=new ArrayList<>();
        when(ordersRepo.findAllByCustomerUid(customer.getUid(), Pageable.ofSize(1))).thenReturn(new PageImpl<>(list1));
        ResponseMessageWithPagination responseMessage1 = customerService.getAllOrders(1,1,request).getBody();
        assert responseMessage1 != null;
        assertEquals("No orders", responseMessage1.getData());
    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_ALL_HIS_ORDERS_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> customerService.getAllOrders(1,1,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_ALL_HIS_ORDERS_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> customerService.getAllOrders(1,1,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_ALL_DRIVES_SCHEDULED_IN_HIS_CITY_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<EWasteDrive> listDrive =new ArrayList<>();
        listDrive.add(eWasteDrive);
        when(eWasteDriveRepo.findByStatus("Upcoming")).thenReturn((listDrive));

        try (MockedStatic<Util> mockedStatic = Mockito.mockStatic(Util.class)) {
            mockedStatic.when(() -> Util.checkExpiryOfRequest("2022-01-20")).thenReturn(true);
        }

        try (MockedStatic<Util> mockedStatic = Mockito.mockStatic(Util.class)) {
            mockedStatic.when(() -> Util.checkExpiryOfRequest("2022-01-20")).thenReturn(false);
        }

        when(eWasteDriveRepo.findAllByCity(customer.getCity())).thenReturn(listDrive);
        ResponseMessage responseMessage = customerService.viewDrives(request).getBody();
        assert responseMessage != null;
        List<EWasteDrive>drives= (List<EWasteDrive>) responseMessage.getData();
        assertEquals(listDrive.get(0).getCollectorEmail(),drives.get(0).getCollectorEmail());
    }

    @Test
    void WHEN_DRIVES_SCHEDULED_IN_HIS_CITY_IS_EXPIRED_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<EWasteDrive> listDrive =new ArrayList<>();
        listDrive.add(eWasteDrive1);
        when(eWasteDriveRepo.findByStatus("Upcoming")).thenReturn((listDrive));

        try (MockedStatic<Util> mockedStatic = Mockito.mockStatic(Util.class)) {
            mockedStatic.when(() -> Util.checkExpiryOfRequest("2022-01-20")).thenReturn(true);
        }

        try (MockedStatic<Util> mockedStatic = Mockito.mockStatic(Util.class)) {
            mockedStatic.when(() -> Util.checkExpiryOfRequest("2022-01-20")).thenReturn(false);
        }

        when(eWasteDriveRepo.findAllByCity(customer.getCity())).thenReturn(listDrive);
        ResponseMessage responseMessage = customerService.viewDrives(request).getBody();
        assert responseMessage != null;
        List<EWasteDrive>drives= (List<EWasteDrive>) responseMessage.getData();
        assertEquals(listDrive.get(0).getCollectorEmail(),drives.get(0).getCollectorEmail());
    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_ALL_DRIVES_SCHEDULED_IN_HIS_CITY_Failure() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<EWasteDrive> listDriveCity1=new ArrayList<>();
        when(eWasteDriveRepo.findAllByCity(customer.getCity())).thenReturn(listDriveCity1);
        ResponseMessage responseMessage1 = customerService.viewDrives(request).getBody();

        assert responseMessage1 != null;
        assertEquals("No Drives In your Area", responseMessage1.getData());
    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_ALL_DRIVES_SCHEDULED_IN_HIS_CITY_THROWS_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> customerService.viewDrives(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_ALL_DRIVES_SCHEDULED_IN_HIS_CITY_THROWS_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> customerService.viewDrives(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }
    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_COLLECTOR_DETAILS_Success() {

        when(userRepo.findUserByUid(collector.getUid())).thenReturn(collector);
        ResponseMessage responseMessage = customerService.viewCollectorProfile(collector.getUid()).getBody();
        assert responseMessage != null;
        User collector1=(User) responseMessage.getData();
        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));
        assertEquals(collector.getUid(),collector1.getUid());
    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_COLLECTOR_DETAILS_Failure() {

        when(userRepo.findUserByUid(collector.getUid())).thenReturn(null);
        ResponseMessage responseMessage1 = customerService.viewCollectorProfile(collector.getUid()).getBody();
        assert responseMessage1 != null;
        assertEquals("Order Not Accepted By Collector", responseMessage1.getData());

    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_HIS_NOTIFICATION_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        Notification customerNotification=new Notification();
        customerNotification.setCustomerUid(customer.getUid());
        customerNotification.setRole("Customer");
        customerNotification.setMessage("TEST Message");
        customerNotification.setStatus(false);

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(customerNotification);
        when(notificationRepo.findByRoleAndStatusAndCustomerUid("Customer",false,customer.getUid())).thenReturn(notificationList);

        ResponseMessage responseMessage = customerService.viewNotification(request).getBody();
        assert responseMessage != null;
        List<Notification>customerNotification1= (List<Notification>) responseMessage.getData();
        assertEquals(notificationList.get(0).getCustomerUid(),customerNotification1.get(0).getCustomerUid());
    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_HIS_NOTIFICATION_Failure() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());
        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<Notification> notificationList = new ArrayList<>();
        when(notificationRepo.findByRoleAndStatusAndCustomerUid("Customer",false,customer.getUid())).thenReturn(notificationList);

        ResponseMessage responseMessage = customerService.viewNotification(request).getBody();
        assert responseMessage != null;
        assertEquals(NO_NEW_NOTIFICATION, responseMessage.getData());
    }

    @Test
    void vWHEN_CUSTOMER_WANT_TO_VIEW_HIS_NOTIFICATION_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> customerService.viewNotification(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_VIEW_HIS_NOTIFICATION_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> customerService.viewNotification(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_READ_HIS_NOTIFICATION_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        Notification customerNotification = new Notification();
        customerNotification.setCustomerUid(customer.getUid());
        customerNotification.setMessage("TEST Message");
        customerNotification.setStatus(false);

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(customerNotification);
        when(notificationRepo.findByRoleAndStatusAndCustomerUid("Customer", false,customer.getUid())).thenReturn(notificationList);

        ResponseMessage responseMessage = customerService.readNotification(request).getBody();

        assert responseMessage != null;
        List<Notification> customerNotification1 = (List<Notification>) responseMessage.getData();

        assertEquals(notificationList.get(0).getCustomerUid(), customerNotification1.get(0).getCustomerUid());
    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_READ_HIS_NOTIFICATION_Failure() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<Notification> notificationList = new ArrayList<>();
        when(notificationRepo.findByRoleAndStatusAndCustomerUid("Customer", false,customer.getUid())).thenReturn(notificationList);

        ResponseMessage responseMessage = customerService.readNotification(request).getBody();

        assert responseMessage != null;
        assertEquals(NO_UNREAD_NOTIFICATION, responseMessage.getData());
    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_READ_HIS_NOTIFICATION_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> customerService.readNotification(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_CUSTOMER_WANT_TO_READ_HIS_NOTIFICATION_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> customerService.readNotification(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void getAnalytics()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<Orders>orderInCity= new ArrayList<>();
        orderInCity.add(orders);
        when(ordersRepo.findAllByCity(customer.getCity())).thenReturn(orderInCity);

        List<Orders>orderCustomer= new ArrayList<>();
        orderCustomer.add(orders);
        when(ordersRepo.findAllAnalyticsByCustomerUid(customer.getUid())).thenReturn(orderCustomer);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("orderInCity",orderInCity.size());
        hashMap.put("orderCustomer",orderCustomer.size());

        ResponseMessage responseMessage = customerService.getAnalytics(request).getBody();
        HashMap<String,Integer> hashMap1 = (HashMap) responseMessage.getData();

        assertEquals(hashMap.get("orderInCity"),hashMap1.get("orderInCity"));

    }

    @Test
    void getAnalytics_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> customerService.getAnalytics(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void getAnalytics_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> customerService.getAnalytics(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void getAnalyticsV2()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<EWasteDrive> eWasteDriveListCity= new ArrayList<>();
        eWasteDriveListCity.add(eWasteDrive);
        when(eWasteDriveRepo.findAllByCity(customer.getCity())).thenReturn(eWasteDriveListCity);

        List<EWasteDrive>eWasteDriveListAll= new ArrayList<>();
        eWasteDriveListAll.add(eWasteDrive);
        when(eWasteDriveRepo.findAll()).thenReturn(eWasteDriveListAll);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put(E_WASTE_DRIVE_LIST_CITY,eWasteDriveListCity.size());
        hashMap.put(E_WASTE_DRIVE_LIST_ALL,eWasteDriveListAll.size());

        ResponseMessage responseMessage = customerService.getAnalyticsV2(request).getBody();
        assert responseMessage != null;
        HashMap<String,Integer> hashMap1 = (HashMap) responseMessage.getData();

        assertEquals(hashMap.get(E_WASTE_DRIVE_LIST_CITY),hashMap1.get(E_WASTE_DRIVE_LIST_CITY));

    }

    @Test
    void getAnalyticsV2_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> customerService.getAnalyticsV2(request)
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
                InvalidUserException.class, () -> customerService.getAnalyticsV2(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void getAnalyticsForCollectorInCity(){
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

        UserDetails userDetails=new UserDetails();
        userDetails.setUser(collector);
        userDetails.setCategoriesAccepted(set);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        when(userDetailsRepo.findUserByUid(collector.getUid())).thenReturn(userDetails);

        List<User> collectorList =new ArrayList<>();
        collectorList.add(collector);
        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,customer.getCity())).thenReturn(collectorList);

        List<User> collectorTotalList = new ArrayList<>();
        collectorTotalList.add(collector);
        when(userRepo.findAll()).thenReturn(collectorTotalList);

        List<CategoriesAccepted> listTemp =new ArrayList<>();
        listTemp.add(categoriesAcceptedTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted("Temp", collector.getId())).thenReturn(listTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, collectorTotalList.get(0).getId())).thenReturn(listTemp);

        ResponseMessage responseMessage = customerService.getAnalyticsForCollectorInCity(request).getBody();
        assert responseMessage != null;
        HashMap<String,Integer>hashMapEnvelope= (HashMap<String, Integer>) responseMessage.getData();

        assertEquals(1,hashMapEnvelope.get(TEMP_CITY));
    }

    @Test
    void getAnalyticsForAllCollector(){
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

        UserDetails userDetails=new UserDetails();
        userDetails.setUser(collector);
        userDetails.setCategoriesAccepted(set);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        request.addHeader("Authorization", AUTHORIZATION_TOKEN_TESTING);

        when(jwtUtil.fetchId(request.getHeader("Authorization"))).thenReturn(customer.getEmail());

        when(userDetailsRepo.findUserByUid(collector.getUid())).thenReturn(userDetails);

        List<User> collectorTotalList = new ArrayList<>();
        collectorTotalList.add(collector);
        when(userRepo.findAllUsersByRole(COLLECTOR)).thenReturn(collectorTotalList);

        List<CategoriesAccepted> listTemp =new ArrayList<>();
        listTemp.add(categoriesAcceptedTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted("Temp", collector.getId())).thenReturn(listTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, collectorTotalList.get(0).getId())).thenReturn(listTemp);

        ResponseMessage responseMessage = customerService.getAnalyticsForAllCollector(request).getBody();
        assert responseMessage != null;
        HashMap<String,Integer> hashMapEnvelope= (HashMap<String, Integer>) responseMessage.getData();

        assertEquals(1,hashMapEnvelope.get(TEMP_TOTAL));
    }


    @Test
    void getAnalyticsForCollectorInCity_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> customerService.getAnalyticsForCollectorInCity(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void getAnalyticsForCollectorInCity_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> customerService.getAnalyticsForCollectorInCity(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void getAnalyticsForAllCollector_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> customerService.getAnalyticsForAllCollector(request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void getAnalyticsForAllCollector_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(EMAIL,customer.getEmail());
        when(userRepo.findAllUsersByRole(COLLECTOR)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> customerService.getAnalyticsForAllCollector(request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }
}