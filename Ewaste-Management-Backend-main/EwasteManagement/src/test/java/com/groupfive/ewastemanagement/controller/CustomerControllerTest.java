package com.groupfive.ewastemanagement.controller;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.collectorentity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.collectorentity.EWasteDrive;
import com.groupfive.ewastemanagement.entity.collectorentity.EWasteDriveCategories;
import com.groupfive.ewastemanagement.entity.customerentity.Customer;
import com.groupfive.ewastemanagement.entity.customerentity.Orders;
import com.groupfive.ewastemanagement.entity.notification.Notification;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.model.RequestModel;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.repository.collectorrepository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.EWasteDriveRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.CustomerRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.OrdersRepo;
import com.groupfive.ewastemanagement.repository.notification.NotificationRepo;
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
class CustomerControllerTest {

    @Autowired
    private CustomerController customerController;

    @MockBean
    private CustomerRepo customerRepo;

    @MockBean
    private OrdersRepo ordersRepo;

    @MockBean
    private CollectorRepo collectorRepo;

    @MockBean
    private CategoriesAcceptedRepo categoriesAcceptedRepo;

    @MockBean
    private EWasteDriveRepo eWasteDriveRepo;

    @MockBean
    private NotificationRepo notificationRepo;

    Customer customer;
    Collector collector;
    RequestModel requestModel;
    Orders orders;
    EWasteDrive eWasteDrive;
    @BeforeAll
    public void setUp()
    {
        customer = new Customer();
        customer.setFirstName("Siri");
        customer.setLastName("kumar");
        customer.setEmail("customer@gmail.com");
        customer.setCity("london");
        customer.setState("london");
        customer.setAddress1("Hno 5");
        customer.setMobileNo("9875464777");
        customer.setPassword("123456");
        customer.setPinCode("125005");
        customer.setUid(customer.getUid());

        collector = new Collector();
        collector.setFirstName("alexa");
        collector.setLastName("singh");
        collector.setEmail("collector@gmail.com");
        collector.setCity("london");
        collector.setState("london");
        collector.setAddress1("Hno 6");
        collector.setMobileNo("8987454879");
        collector.setPassword("123456");
        collector.setPinCode("125005");
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted("Laptop");

        Set<CategoriesAccepted>set=new HashSet<>();
        set.add(categoriesAccepted);
        collector.setCategoriesAcceptedSet(set);

        collector.setUid(collector.getUid());

        requestModel = new RequestModel();
        requestModel.setRequestType(Constants.PICK_UP);
        requestModel.setQuantity("20");
        requestModel.setCategory("IT Appliances");
        requestModel.setItemName("Laptop");
        requestModel.setScheduledDate("2022-03-19");
        requestModel.setScheduledTime("14:13");
        requestModel.setCollectorUid(collector.getUid());

        orders = new Orders();
        orders.setCategory(requestModel.getCategory());
        orders.setQuantity(requestModel.getQuantity());
        orders.setRequestType(Constants.PICK_UP);
        orders.setPinCode(customer.getPinCode());
        orders.setCustomerUid(customer.getUid());
        orders.setCollectorUid(collector.getUid());
        orders.setStatus(Constants.PENDING);
        orders.setCity(customer.getCity());
        orders.setState(customer.getState());
        orders.setAddress(customer.getAddress1());
        orders.setOrderUid(orders.getOrderUid());
        orders.setItemName(requestModel.getItemName());
        orders.setScheduledDate(requestModel.getScheduledDate());
        orders.setScheduledTime(requestModel.getScheduledTime());


        eWasteDrive = new EWasteDrive();
        eWasteDrive.setDriveName("XYZ");
        eWasteDrive.setDescription("yyyy");
        eWasteDrive.setOrganizerName("KOOK");
        EWasteDriveCategories categoriesAccepted1 = new EWasteDriveCategories();
        categoriesAccepted.setCategoryAccepted("IT Appliances");
        Set<EWasteDriveCategories> set1=new HashSet<>();
        set1.add(categoriesAccepted1);
        eWasteDrive.seteWasteCategoryAccepted(set1);
        eWasteDrive.setDate("2022-08-23");
        eWasteDrive.setTime("07:23");
        eWasteDrive.setLocation(collector.getCity());
        eWasteDrive.setCity("London");
        eWasteDrive.setStatus("Upcoming");
        eWasteDrive.setCollectorEmail(collector.getEmail());
    }

    @Test
    void getAllOrders_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        List<Orders> list=new ArrayList<>();
        list.add(orders);
        when(ordersRepo.findAllByCustomerUid(customer.getUid())).thenReturn(list);
        EnvelopeMessage envelopeMessage= (EnvelopeMessage) customerController.getAllOrders(request).getBody();
        List<Orders>ordersList= (List<Orders>) envelopeMessage.getData();
        assertEquals(orders.getId(),ordersList.get(0).getId());

    }

    @Test
    void getAllOrders_Failure() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        List<Orders> list1=new ArrayList<>();
        when(ordersRepo.findAllByCustomerUid(customer.getUid())).thenReturn(list1);
        EnvelopeMessage envelopeMessage1= (EnvelopeMessage) customerController.getAllOrders(request).getBody();
        assertEquals("No orders",envelopeMessage1.getData());
    }

    @Test
    void viewProfile() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        EnvelopeMessage envelopeMessage=(EnvelopeMessage)customerController.viewProfile(request).getBody();

        Customer c=(Customer) envelopeMessage.getData();

        assertEquals(customer.getEmail(), c.getEmail());
    }

    @Test
    void editProfile() {

        UserModel userModel=new UserModel();
        userModel.setFirstName("Alex");
        userModel.setLastName("Singh");
        userModel.setEmail("alexbob@gmail.com");
        userModel.setCity("London");
        userModel.setState("London");
        userModel.setAddress1("Hno 1");
        userModel.setMobileNo("9050505050");
        userModel.setPassword("123456");
        userModel.setPinCode("125005");

        Customer customer= new Customer();
        customer.setFirstName(userModel.getFirstName());
        customer.setLastName(userModel.getLastName());
        customer.setEmail(userModel.getEmail());
        customer.setCity(userModel.getCity());
        customer.setState(userModel.getState());
        customer.setAddress1(userModel.getAddress1());
        customer.setMobileNo(userModel.getMobileNo());
        customer.setPassword(userModel.getPassword());
        customer.setPinCode(userModel.getPinCode());
        customer.setUid(customer.getUid());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        EnvelopeMessage envelopeMessage=(EnvelopeMessage)customerController.editProfile(userModel,request).getBody();

        Customer c=(Customer) envelopeMessage.getData();

        assertEquals(customer.getEmail(), c.getEmail());
    }

    @Test
    void viewDrives_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        List<EWasteDrive> listDrive =new ArrayList<>();
        listDrive.add(eWasteDrive);
        when(eWasteDriveRepo.findByStatus("Upcoming")).thenReturn((listDrive));

        List<EWasteDrive> listDriveCity=new ArrayList<>();
        listDriveCity.add(eWasteDrive);
        when(eWasteDriveRepo.findAllByCity(customer.getCity())).thenReturn(listDriveCity);
        EnvelopeMessage envelopeMessage= (EnvelopeMessage) customerController.viewEWasteDrives(request).getBody();
        List<EWasteDrive>drives= (List<EWasteDrive>) envelopeMessage.getData();
        assertEquals(listDrive.get(0).getCollectorEmail(),drives.get(0).getCollectorEmail());
    }

    @Test
    void viewDrives_Failure() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        List<EWasteDrive> listDriveCity1=new ArrayList<>();
        when(eWasteDriveRepo.findAllByCity(customer.getCity())).thenReturn(listDriveCity1);
        EnvelopeMessage envelopeMessage1= (EnvelopeMessage) customerController.viewEWasteDrives(request).getBody();
        assertEquals("No Drives In your Area",envelopeMessage1.getData());
    }

    @Test
    void viewNotification_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        Notification customerNotification=new Notification();
        customerNotification.setCustomerUid(customer.getUid());
        customerNotification.setRole("Customer");
        customerNotification.setMessage("TEST Message");
        customerNotification.setStatus(false);

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(customerNotification);
        when(notificationRepo.findByRoleAndStatusAndCustomerUid("Customer",false,customer.getUid())).thenReturn(notificationList);

        EnvelopeMessage envelopeMessage=(EnvelopeMessage) customerController.viewNotification(request).getBody();
        List<Notification>customerNotification1= (List<Notification>) envelopeMessage.getData();
        assertEquals(notificationList.get(0).getCustomerUid(),customerNotification1.get(0).getCustomerUid());
    }

    @Test
    void viewNotification_Failure() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        List<Notification> notificationList = new ArrayList<>();
        when(notificationRepo.findByRoleAndStatusAndCustomerUid("Customer",false,customer.getUid())).thenReturn(notificationList);

        EnvelopeMessage envelopeMessage=(EnvelopeMessage) customerController.viewNotification(request).getBody();
        assertEquals(NO_NEW_NOTIFICATION,envelopeMessage.getData());
    }

    @Test
    void viewCollectorProfile_Success() {

        when(collectorRepo.findByUid(collector.getUid())).thenReturn(collector);
        EnvelopeMessage envelopeMessage= (EnvelopeMessage) customerController.viewCollectorProfile(collector.getUid()).getBody();
        Collector collector1=(Collector)envelopeMessage.getData();
        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));
        assertEquals(collector.getUid(),collector1.getUid());
    }

    @Test
    void viewCollectorProfile_Failure() {

        when(collectorRepo.findByUid(collector.getUid())).thenReturn(null);
        EnvelopeMessage envelopeMessage1= (EnvelopeMessage) customerController.viewCollectorProfile(collector.getUid()).getBody();
        assertEquals("Order Not Accepted By Collector",envelopeMessage1.getData());

    }

    @Test
    void readNotification_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        Notification customerNotification = new Notification();
        customerNotification.setCustomerUid(customer.getUid());
        customerNotification.setMessage("TEST Message");
        customerNotification.setStatus(false);

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(customerNotification);
        when(notificationRepo.findByRoleAndStatusAndCustomerUid("Customer", false,customer.getUid())).thenReturn(notificationList);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) customerController.readNotification(request).getBody();

        List<Notification> customerNotification1 = (List<Notification>) envelopeMessage.getData();

        assertEquals(notificationList.get(0).getCustomerUid(), customerNotification1.get(0).getCustomerUid());
    }

    @Test
    void readNotification_Failure() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        List<Notification> notificationList = new ArrayList<>();
        when(notificationRepo.findByRoleAndStatusAndCustomerUid("Customer", false,customer.getUid())).thenReturn(notificationList);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) customerController.readNotification(request).getBody();

        assertEquals(NO_UNREAD_NOTIFICATION, envelopeMessage.getData());
    }

    @Test
    void getAnalytics()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        List<Orders>orderInCity= new ArrayList<>();
        orderInCity.add(orders);
        when(ordersRepo.findAllByCity(customer.getCity())).thenReturn(orderInCity);

        List<Orders>orderCustomer= new ArrayList<>();
        orderCustomer.add(orders);
        when(ordersRepo.findAllByCustomerUid(customer.getUid())).thenReturn(orderCustomer);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("orderInCity",orderInCity.size());
        hashMap.put("orderCustomer",orderCustomer.size());

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) customerController.getAnalyticsV1(request).getBody();
        HashMap<String,Integer> hashMap1 = (HashMap) envelopeMessage.getData();

        assertEquals(hashMap.get("orderInCity"),hashMap1.get("orderInCity"));

    }

    @Test
    void getAnalyticsV2()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        List<EWasteDrive> eWasteDriveListCity= new ArrayList<>();
        eWasteDriveListCity.add(eWasteDrive);
        when(eWasteDriveRepo.findAllByCity(customer.getCity())).thenReturn(eWasteDriveListCity);

        List<EWasteDrive>eWasteDriveListAll= new ArrayList<>();
        eWasteDriveListAll.add(eWasteDrive);
        when(eWasteDriveRepo.findAll()).thenReturn(eWasteDriveListAll);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("eWasteDriveListCity",eWasteDriveListCity.size());
        hashMap.put("eWasteDriveListAll",eWasteDriveListAll.size());

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) customerController.getAnalyticsV2(request).getBody();
        HashMap<String,Integer> hashMap1 = (HashMap) envelopeMessage.getData();

        assertEquals(hashMap.get("eWasteDriveListCity"),hashMap1.get("eWasteDriveListCity"));

    }

    @Test
    void getAnalyticsV3(){

        Set<CategoriesAccepted>set=new HashSet<>();
        CategoriesAccepted categoriesAcceptedTemp=new CategoriesAccepted();
        categoriesAcceptedTemp.setId(1L);
        categoriesAcceptedTemp.setCategoryAccepted("Temp");

        CategoriesAccepted categoriesAcceptedLapms=new CategoriesAccepted();
        categoriesAcceptedLapms.setId(2L);
        categoriesAcceptedLapms.setCategoryAccepted("Lapms");

        CategoriesAccepted categoriesAcceptedLargeEquip=new CategoriesAccepted();
        categoriesAcceptedLargeEquip.setId(3L);
        categoriesAcceptedLargeEquip.setCategoryAccepted("LargeEqip");

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
        set.add(categoriesAcceptedLapms);
        set.add(categoriesAcceptedLargeEquip);
        set.add(categoriesAcceptedSmallEquip);
        set.add(categoriesAcceptedSmallIT);
        set.add(categoriesAcceptedScreens);

        collector.setCategoriesAcceptedSet(set);


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        List<Collector> collectorList =new ArrayList<>();
        collectorList.add(collector);
        when(collectorRepo.findByCity(customer.getCity())).thenReturn(collectorList);

        List<Collector> collectorTotalList = new ArrayList<>();
        collectorTotalList.add(collector);
        when(collectorRepo.findAll()).thenReturn(collectorTotalList);

        List<CategoriesAccepted> listTemp =new ArrayList<>();
        listTemp.add(categoriesAcceptedTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted("Temp", collector.getId())).thenReturn(listTemp);

        List<CategoriesAccepted> listLapms =new ArrayList<>();
        listLapms.add(categoriesAcceptedTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted("Lapms", collector.getId())).thenReturn(listLapms);

        List<CategoriesAccepted> listLargeEqip =new ArrayList<>();
        listLargeEqip.add(categoriesAcceptedTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted("LargeEqip", collector.getId())).thenReturn(listLargeEqip);

        List<CategoriesAccepted> listSmallEqip =new ArrayList<>();
        listSmallEqip.add(categoriesAcceptedTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted("SmallEquip", collector.getId())).thenReturn(listSmallEqip);

        List<CategoriesAccepted> listSmallIT =new ArrayList<>();
        listSmallIT.add(categoriesAcceptedTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted("SmallIT", collector.getId())).thenReturn(listSmallIT);

        List<CategoriesAccepted> listScreens =new ArrayList<>();
        listScreens.add(categoriesAcceptedTemp);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted("Screens", collector.getId())).thenReturn(listScreens);


        EnvelopeMessage envelopeMessage=(EnvelopeMessage) customerController.getAnalyticsV3(request).getBody();
        HashMap<String,Integer>hashMapEnvelope= (HashMap<String, Integer>) envelopeMessage.getData();

        assertEquals(1,hashMapEnvelope.get(TEMP_CITY));
        assertEquals(1,hashMapEnvelope.get(LAPMS_CITY));
        assertEquals(1,hashMapEnvelope.get(LARGE_EQIP_CITY));
        assertEquals(1,hashMapEnvelope.get(SMALL_EQUIP_CITY));
        assertEquals(1,hashMapEnvelope.get(SMALL_IT_CITY));
        assertEquals(1,hashMapEnvelope.get(SCREENS_CITY));
    }
}