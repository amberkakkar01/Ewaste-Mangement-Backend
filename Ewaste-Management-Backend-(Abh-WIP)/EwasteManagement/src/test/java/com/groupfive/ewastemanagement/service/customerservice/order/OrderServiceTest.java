package com.groupfive.ewastemanagement.service.customerservice.order;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.entity.AllPendingRequest;
import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.EWasteDrive;
import com.groupfive.ewastemanagement.entity.UserDetails;
import com.groupfive.ewastemanagement.entity.Orders;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.dto.request.RequestDTO;
import com.groupfive.ewastemanagement.repository.AllPendingRequestRepo;
import com.groupfive.ewastemanagement.repository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.UserDetailsRepo;
import com.groupfive.ewastemanagement.repository.OrdersRepo;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.service.CustomerService;
import com.groupfive.ewastemanagement.service.OrderService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceTest {
    CustomerService customerService;
    OrderService orderService;

    @Autowired
    public OrderServiceTest(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @MockBean
    UserRepo userRepo;

    @MockBean
    OrdersRepo ordersRepo;

    @MockBean
    CategoriesAcceptedRepo categoriesAcceptedRepo;

    @MockBean
    AllPendingRequestRepo allPendingRequestRepo;

    @MockBean
    UserDetailsRepo userDetailsRepo;

    User customer;
    User collector;
    RequestDTO requestDTO;
    Orders orders;
    EWasteDrive eWasteDrive;
    CategoriesAccepted categoriesAccepted;
    AllPendingRequest allPendingRequest;
    UserDetails userDetails;
    @BeforeAll
    public void setUp()
    {
        categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setId(1L);
        categoriesAccepted.setCategoryAccepted("dummy");

        Set<CategoriesAccepted>set=new HashSet<>();
        set.add(categoriesAccepted);

        userDetails=new UserDetails();
        userDetails.setCategoriesAccepted(set);
        userDetails.setUser(collector);

        userDetails.setCategoriesAccepted(set);

        customer = new User();
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

        collector = new User();
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

        requestDTO = new RequestDTO();
        requestDTO.setRequestType(Constants.PICK_UP);
        requestDTO.setQuantity("20");
        requestDTO.setCategory(categoriesAccepted);
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
        orders.setCategory(categoriesAccepted);
        orders.setOrderUid(orders.getOrderUid());
        orders.setItemName(requestDTO.getItemName());
        orders.setScheduledDate(requestDTO.getScheduledDate());
        orders.setScheduledTime(requestDTO.getScheduledTime());


        eWasteDrive = new EWasteDrive();
        eWasteDrive.setDriveName("E-Waste Drive");
        eWasteDrive.setDescription("Drive organized to donate E-Waste");
        eWasteDrive.setOrganizerName(collector.getFirstName());
        eWasteDrive.setEWasteCategoryAccepted(set);
        eWasteDrive.setDate("2022-08-23");
        eWasteDrive.setTime("07:23");
        eWasteDrive.setLocation(collector.getCity());
        eWasteDrive.setCity("Bangalore");
        eWasteDrive.setStatus("Upcoming");
        eWasteDrive.setCollectorEmail(collector.getEmail());

        allPendingRequest=new AllPendingRequest();
    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_CREATE_A_PICKUP_REQUEST_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        CategoriesAccepted accepted=new CategoriesAccepted();
        accepted.setCategoryAccepted("dummy");
        accepted.setId(1L);

        List<User>list=new ArrayList<>();
        list.add(collector);


        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn(customer);


        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,customer.getCity())).thenReturn(list);

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted())).thenReturn(categoriesAccepted);

        when(userRepo.findAllUsersByCat(categoriesAccepted.getCategoryAccepted())).thenReturn(list);

        when(allPendingRequestRepo.save(allPendingRequest)).thenReturn(allPendingRequest);

        ResponseMessage responseMessage=orderService.createPickUpRequest(requestDTO,request).getBody();
        assert responseMessage != null;
        Orders orders1= (Orders) responseMessage.getData();

        assertEquals(customer.getUid(),orders1.getCustomerUid());

    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_CREATE_A_PICKUP_REQUEST_Failure() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());
        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(requestDTO.getCategory().getCategoryAccepted())).thenReturn(categoriesAccepted);

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        List<User> list = new ArrayList<>();
        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,customer.getCity())).thenReturn(list);

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted())).thenReturn(categoriesAccepted);
        List<UserDetails> categoryList=new ArrayList<>();
        categoryList.add(userDetails);
        when(userDetailsRepo.findUserDetailsByCategoriesAcceptedId(categoriesAccepted.getId())).thenReturn(categoryList);

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted())).thenReturn(categoriesAccepted);


        ResponseMessage responseMessage = orderService.createPickUpRequest(requestDTO, request).getBody();

        //changed
        assert responseMessage != null;
        assertEquals(NO_COLLECTOR_FOUND, responseMessage.getData());
    }


    @Test
    void WHEN_A_CUSTOMER_WANT_TO_CREATE_A_PICKUP_REQUEST_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> orderService.createPickUpRequest(requestDTO,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_CREATE_A_PICKUP_REQUEST_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> orderService.createPickUpRequest(requestDTO,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_VIEW_DROP_OFF_LOCATION() {

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        List<User> list = new ArrayList<>();
        list.add(collector);
        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,customer.getCity())).thenReturn(list);

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted())).thenReturn(categoriesAccepted);
        List<UserDetails> categoryList=new ArrayList<>();
        categoryList.add(userDetails);
        when(userDetailsRepo.findUserDetailsByCategoriesAcceptedId(categoriesAccepted.getId())).thenReturn(categoryList);

        when(userRepo.findUserByUid(categoryList.get(0).getUid())).thenReturn(collector);

        assertEquals(1, orderService.showDropOffLocation(categoriesAccepted.getCategoryAccepted(), request));
    }
    @Test
    void WHEN_A_CUSTOMER_WANT_TO_VIEW_DROP_OFF_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> orderService.showDropOffLocation(SMALL_EQUIP,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_VIEW_DROP_OFF_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> orderService.showDropOffLocation(SMALL_EQUIP,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_VIEW_LIST_OF_COLLECTOR_AVAILABLE_FOR_DROP_OFF() {

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        List<User> list = new ArrayList<>();
        list.add(collector);
        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,customer.getCity())).thenReturn(list);

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted())).thenReturn(categoriesAccepted);
        List<UserDetails> categoryList=new ArrayList<>();
        categoryList.add(userDetails);
        when(userDetailsRepo.findUserDetailsByCategoriesAcceptedId(categoriesAccepted.getId())).thenReturn(categoryList);

        //changed
        when(userRepo.findUserByUid(categoryList.get(0).getUid())).thenReturn(collector);

        ResponseMessage responseMessage = orderService.viewListOfCollectorsForDropOff(categoriesAccepted.getCategoryAccepted(), request).getBody();

        assert responseMessage != null;
        List<User> listOfCollectors = (List<User>) responseMessage.getData();
        assertEquals(collector.getUid(), listOfCollectors.get(0).getUid());
    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_VIEW_LIST_OF_COLLECTOR_AVAILABLE_FOR_DROP_OFF_Failure() {

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        List<User> list = new ArrayList<>();
        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,customer.getCity())).thenReturn(list);

        List<String> stringList = new ArrayList<>();
        stringList.add(collector.getUid());

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted())).thenReturn(categoriesAccepted);
        List<UserDetails> categoryList=new ArrayList<>();
        categoryList.add(userDetails);
        when(userDetailsRepo.findUserDetailsByCategoriesAcceptedId(categoriesAccepted.getId())).thenReturn(categoryList);

        //changed
        when(userRepo.findUserByUid(stringList.get(0))).thenReturn(collector);

        ResponseMessage responseMessage = orderService.viewListOfCollectorsForDropOff(categoriesAccepted.getCategoryAccepted(), request).getBody();

        assert responseMessage != null;
        assertEquals("No Collector in your area accepting entered category", responseMessage.getData());
    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_VIEW_LIST_OF_COLLECTOR_AVAILABLE_FOR_DROP_OFF_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> orderService.viewListOfCollectorsForDropOff(SMALL_EQUIP,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_VIEW_LIST_OF_COLLECTOR_AVAILABLE_FOR_DROP_OFF_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> orderService.viewListOfCollectorsForDropOff(SMALL_EQUIP,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_COUNT_COLLECTOR_AVAILABLE_FOR_PICKUP_SUCCESS() {

        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        List<User> list = new ArrayList<>();
        list.add(collector);
        when(userRepo.findAllUsersByRoleAndCity(COLLECTOR,customer.getCity())).thenReturn(list);

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted())).thenReturn(categoriesAccepted);
        List<UserDetails> categoryList=new ArrayList<>();
        categoryList.add(userDetails);
        when(userDetailsRepo.findUserDetailsByCategoriesAcceptedId(categoriesAccepted.getId())).thenReturn(categoryList);

        assertEquals(1, orderService.countCollectorPickUp((categoriesAccepted.getCategoryAccepted()), request));
    }


    @Test
    void WHEN_A_CUSTOMER_WANT_TO_COUNT_COLLECTOR_AVAILABLE_FOR_PICKUP_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> orderService.countCollectorPickUp(SMALL_EQUIP,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_COUNT_COLLECTOR_AVAILABLE_FOR_PICKUP_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> orderService.countCollectorPickUp(SMALL_EQUIP,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }
    @Test
    void WHEN_A_CUSTOMER_WANT_TO_CREATE_DROP_OFF_REQUEST_SUCCESS() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(requestDTO.getCategory().getCategoryAccepted())).thenReturn(categoriesAccepted);
        when(userRepo.findUserByEmail(customer.getEmail())).thenReturn((customer));
        when(ordersRepo.save(orders)).thenReturn(orders);

        ResponseMessage responseMessage = orderService.createDropOffRequest(requestDTO, request).getBody();

        assert responseMessage != null;
        Orders serviceOrders = (Orders) responseMessage.getData();
        assertEquals(orders.getCollectorUid(), serviceOrders.getCollectorUid());

    }


    @Test
    void WHEN_A_CUSTOMER_WANT_TO_CREATE_DROP_OFF_REQUEST_BadRequestException()throws BadRequestException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> orderService.createDropOffRequest(requestDTO,request)
        );

        assertEquals("Email cannot be empty", exception.getMessage());

    }

    @Test
    void WHEN_A_CUSTOMER_WANT_TO_CREATE_DROP_OFF_REQUEST_InvalidUserException()throws BadRequestException{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String email="test@gmail.com";
        request.addHeader(EMAIL,email);
        when(userRepo.findUserByEmail(email)).thenReturn(null);

        InvalidUserException exception = assertThrows(
                InvalidUserException.class, () -> orderService.createDropOffRequest(requestDTO,request)
        );

        assertEquals("The user does not exist", exception.getMessage());

    }

}
