package com.groupfive.ewastemanagement.service.collectorservice;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.collectorentity.*;
import com.groupfive.ewastemanagement.entity.customerentity.Customer;
import com.groupfive.ewastemanagement.entity.customerentity.Orders;
import com.groupfive.ewastemanagement.entity.notification.Notification;
import com.groupfive.ewastemanagement.entity.vendorentity.Vendor;
import com.groupfive.ewastemanagement.entity.vendorentity.VendorOrders;
import com.groupfive.ewastemanagement.model.EWasteDriveModel;
import com.groupfive.ewastemanagement.model.SellItemModel;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.repository.collectorrepository.AllPendingRequestRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.EWasteDriveRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.SellItemRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.CustomerRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.OrdersRepo;
import com.groupfive.ewastemanagement.repository.notification.NotificationRepo;
import com.groupfive.ewastemanagement.repository.vendorrepository.VendorOrdersRepo;
import com.groupfive.ewastemanagement.repository.vendorrepository.VendorRepo;
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
class CollectorServiceTest {

    @MockBean
    private CollectorRepo collectorRepo;

    @Autowired
    private CollectorService collectorService;

    @MockBean
    private AllPendingRequestRepo allPendingRequestRepo;

    @MockBean
    private OrdersRepo ordersRepo;

    @MockBean
    private EWasteDriveRepo eWasteDriveRepo;

    @MockBean
    private SellItemRepo sellItemRepo;

    @MockBean
    private CustomerRepo customerRepo;

    @MockBean
    private NotificationRepo notificationRepo;

    @MockBean
    private VendorOrdersRepo vendorOrdersRepo;

    @MockBean
    private VendorRepo vendorRepo;

    Collector collector;
    AllPendingRequest allPendingRequest;
    AllPendingRequest allPendingRequest1;
    EWasteDriveModel eWasteDriveModel;
    Orders orders;
    Customer customer;
    EWasteDrive eWasteDrive;
    SellItems sellItems;
    SellItemModel sellItemModel;
    Vendor vendor;

    VendorOrders vendorOrders;

    @BeforeAll
    public void setUp()
    {
        customer= new Customer();
        customer.setFirstName("Chaman");
        customer.setLastName("Singh");
        customer.setEmail("chaman@gmail.com");
        customer.setCity("London");
        customer.setState("London");
        customer.setAddress1("Hno 1");
        customer.setMobileNo("9050202020");
        customer.setPassword("123456");
        customer.setPinCode("125005");
        customer.setUid(customer.getUid());

        collector=new Collector();
        collector.setFirstName("Alex");
        collector.setLastName("Singh");
        collector.setEmail("collector@gmail.com");
        collector.setCity("London");
        collector.setState("London");
        collector.setAddress1("Hno 1");
        collector.setMobileNo("9050202020");
        collector.setPassword("123456");
        collector.setPinCode("125005");
        collector.setUid(collector.getUid());

        orders=new Orders();
        orders.setOrderUid("123");
        orders.setStatus("pending");
        orders.setAddress("Hno 1");
        orders.setState("London");
        orders.setCategory("IT Appliances");
        orders.setCity("London");
        orders.setCustomerUid(customer.getUid());
        orders.setPinCode("125005");
        orders.setScheduledDate("2022-02-21");
        orders.setScheduledTime("jhj");
        orders.setQuantity("4");
        orders.setItemName("Laptop");
        orders.setRequestType("pickUp");

        allPendingRequest= new AllPendingRequest();
        allPendingRequest.setOrderId(orders.getOrderUid());
        allPendingRequest.setCollectorUid(collector.getUid());
        allPendingRequest.setStatus("pending");
        allPendingRequest.setCategory("IT Appliances");
        allPendingRequest.setQuantity("4");
        allPendingRequest.setRequestType("pickUp");
        allPendingRequest.setScheduleDate("2022-08-21");
        allPendingRequest.setScheduledTime("jhj");
        allPendingRequest.setItemName("Laptop");

        allPendingRequest1= new AllPendingRequest();
        allPendingRequest1.setOrderId("123");
        allPendingRequest1.setCollectorUid(collector.getUid());
        allPendingRequest1.setStatus("pending");
        allPendingRequest1.setCategory("IT Appliances");
        allPendingRequest1.setQuantity("4");
        allPendingRequest1.setRequestType("pickUp");
        allPendingRequest1.setScheduleDate("2022-02-21");
        allPendingRequest1.setScheduledTime("jhj");
        allPendingRequest1.setItemName("Laptop");

        eWasteDriveModel = new EWasteDriveModel();
        eWasteDriveModel.setDriveName("Demo Drive");
        eWasteDriveModel.setDescription("Demo Drive");
        eWasteDriveModel.setOrganizerName(collector.getFirstName());
        EWasteDriveCategories categoriesAccepted = new EWasteDriveCategories();
        categoriesAccepted.setCategoryAccepted("IT Appliances");

        Set<EWasteDriveCategories> set=new HashSet<>();
        set.add(categoriesAccepted);

        eWasteDriveModel.seteWasteCategoryAccepted(set);
        eWasteDriveModel.setDate("2022-08-23");
        eWasteDriveModel.setTime("07:23");
        eWasteDriveModel.setLocation("BLR");
        eWasteDriveModel.setStatus("upcoming");

        //Drive Creation

        eWasteDrive = new EWasteDrive();
        eWasteDrive.setDriveName(eWasteDriveModel.getDriveName());
        eWasteDrive.setDescription(eWasteDriveModel.getDescription());
        eWasteDrive.setOrganizerName(eWasteDriveModel.getOrganizerName());

        eWasteDrive.seteWasteCategoryAccepted(eWasteDriveModel.geteWasteCategoryAccepted());
        eWasteDrive.setDate(eWasteDriveModel.getDate());
        eWasteDrive.setTime(eWasteDriveModel.getTime());
        eWasteDrive.setLocation(eWasteDriveModel.getLocation());
        eWasteDrive.setStatus(eWasteDriveModel.getStatus());
        eWasteDrive.setCollectorEmail(collector.getEmail());

        sellItemModel = new SellItemModel();
        sellItemModel.setItemName("ABC");
        sellItemModel.setCategory("CAT");
        sellItemModel.setStatus("Available");
        sellItemModel.setPrice("1200");
        sellItemModel.setId(1L);
        sellItemModel.setQuantity("12");

        sellItems = new SellItems();
        sellItems.setId(sellItemModel.getId());
        sellItems.setStatus(sellItemModel.getStatus());
        sellItems.setItemName(sellItemModel.getItemName());
        sellItems.setQuantity(sellItemModel.getQuantity());
        sellItems.setPrice(sellItemModel.getPrice());
        sellItems.setCategory(sellItemModel.getCategory());
        sellItems.setAvailableQuantity(sellItemModel.getQuantity());
        sellItems.setCollectorUid(collector.getUid());

        vendor= new Vendor();
        vendor.setFirstName("Alex");
        vendor.setLastName("singh");
        vendor.setEmail("alexbob@gmail.com");
        vendor.setCity("London");
        vendor.setState("London");
        vendor.setAddress1("Hno 1");
        vendor.setMobileNo("9050202020");
        vendor.setPassword("123456");
        vendor.setPinCode("125005");
        vendor.setUid(vendor.getUid());

        vendorOrders=new VendorOrders();
        vendorOrders.setCollector(collector);
        vendorOrders.setCollectorUid(collector.getUid());
        vendorOrders.setCategory(sellItemModel.getCategory());
        vendorOrders.setQuantity(sellItemModel.getQuantity());
        vendorOrders.setItemName(sellItemModel.getItemName());
        vendorOrders.setPrice(sellItemModel.getPrice());

    }

    @Test
    void getPendingRequest_Success(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn(collector);

        List<AllPendingRequest> allPendingRequestList = new ArrayList<>();
        allPendingRequestList.add(allPendingRequest);
        allPendingRequestList.add(allPendingRequest1);

        when(allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(),"pending")).thenReturn(allPendingRequestList);
        when(ordersRepo.findByOrderUid(allPendingRequest1.getOrderId())).thenReturn(orders);
        EnvelopeMessage envelopeMessage =(EnvelopeMessage) collectorService.getPendingRequest(request).getBody();
        List<AllPendingRequest> allPendingRequestList2 = (List<AllPendingRequest>)envelopeMessage.getData();
        assertEquals(allPendingRequestList.get(0).getCollectorUid(),allPendingRequestList2.get(0).getCollectorUid());

        assertEquals("pending",allPendingRequestList2.get(1).getStatus());
    }

    @Test
    void getPendingRequest_Failure(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn(collector);

        List<AllPendingRequest> allPendingRequestList = new ArrayList<>();
        allPendingRequestList.add(allPendingRequest);
        allPendingRequestList.add(allPendingRequest1);

        List<AllPendingRequest> allPendingRequestList1 = new ArrayList<>();
        when(allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(),"pending")).thenReturn(allPendingRequestList1);
        EnvelopeMessage envelopeMessage2 =(EnvelopeMessage) collectorService.getPendingRequest(request).getBody();
        assertEquals("No Request Pending",envelopeMessage2.getData());
    }

    @Test
    void getRequestSummary_Success(){

        orders.setCollectorUid(collector.getUid());
        orders.setCustomerUid(customer.getUid());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn(collector);
        List<Orders> ordersList1 = new ArrayList<>();
        ordersList1.add(orders);

        when(ordersRepo.findByCollectorUid(collector.getUid())).thenReturn(ordersList1);
        EnvelopeMessage envelopeMessage=(EnvelopeMessage) collectorService.getRequestSummary(request).getBody();
        List<Orders> ordersList = (List<Orders>) envelopeMessage.getData();

        assertEquals(ordersList1.get(0).getCollectorUid(),ordersList.get(0).getCollectorUid());
    }

    @Test
    void getRequestSummary_Failure(){

        orders.setCollectorUid(collector.getUid());
        orders.setCustomerUid(customer.getUid());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn(collector);
        List<Orders> ordersList1 = new ArrayList<>();
        ordersList1.add(orders);

        List<Orders> ordersList2 = new ArrayList<>();
        when(ordersRepo.findByCollectorUid(collector.getUid())).thenReturn(ordersList2);
        EnvelopeMessage envelopeMessage1=(EnvelopeMessage) collectorService.getRequestSummary(request).getBody();

        assertEquals("No orders",envelopeMessage1.getData());
    }

    @Test
    void acceptPendingRequest_Success()
    {
        orders.setCollectorUid(collector.getUid());
        orders.setCustomerUid(customer.getUid());
        orders.setOrderUid(UUID.randomUUID().toString());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));
        when(ordersRepo.findByOrderUid(orders.getOrderUid())).thenReturn(orders);
        when(customerRepo.findByUid(orders.getCustomerUid())).thenReturn(customer);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.acceptPendingRequest(orders.getOrderUid(),request).getBody();
        Orders  orders1 = (Orders)envelopeMessage.getData();
        assertEquals(orders.getCollectorUid(),orders1.getCollectorUid());
    }

    @Test
    void acceptPendingRequest_Failure()
    {
        orders.setCollectorUid(collector.getUid());
        orders.setCustomerUid(customer.getUid());
        orders.setOrderUid(UUID.randomUUID().toString());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));
        when(ordersRepo.findByOrderUid(orders.getOrderUid())).thenReturn(orders);
        when(customerRepo.findByUid(orders.getCustomerUid())).thenReturn(customer);

        when(ordersRepo.findByOrderUid(orders.getOrderUid())).thenReturn(null);
        EnvelopeMessage envelopeMessage1 = (EnvelopeMessage) collectorService.acceptPendingRequest(orders.getOrderUid(),request).getBody();
        assertEquals("No such order exist",envelopeMessage1.getData());

    }

    @Test
    void organizeDrive_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));
        List<Customer> customerList = new ArrayList<>();
        customerList.add(customer);
        when(customerRepo.findByCity(collector.getCity())).thenReturn(customerList);

        EnvelopeMessage envelopeMessage =(EnvelopeMessage) collectorService.organizeDrive(eWasteDriveModel,request).getBody();
        EWasteDrive eWasteDrive1 = (EWasteDrive) envelopeMessage.getData();

        assertEquals(eWasteDrive.getCollectorEmail(),eWasteDrive1.getCollectorEmail());
    }

    @Test
    void organizeDrive_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));
        List<Customer> customerList = new ArrayList<>();
        customerList.add(customer);
        when(customerRepo.findByCity(collector.getCity())).thenReturn(customerList);

        EnvelopeMessage envelopeMessage1 =(EnvelopeMessage) collectorService.organizeDrive(null,request).getBody();
        assertEquals("No data provided",envelopeMessage1.getData());
    }

    @Test
    void sellItem_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn(collector);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.sellItem(sellItemModel,request).getBody();
        SellItems sellItems1 = (SellItems) envelopeMessage.getData();

        assertEquals(sellItems.getCollectorUid(),sellItems1.getCollectorUid());
    }

    @Test
    void sellItem_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn(collector);

        EnvelopeMessage envelopeMessage1 = (EnvelopeMessage) collectorService.sellItem(null,request).getBody();
        assertEquals("Enter all Details",envelopeMessage1.getData());
    }

    @Test
    void viewMyDrive_Success(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn(collector);
        List<EWasteDrive> eWasteDriveList = new ArrayList<>();
        eWasteDriveList.add(eWasteDrive);

        when(eWasteDriveRepo.getAllByCollectorEmail(collector.getEmail())).thenReturn(eWasteDriveList);
        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.viewMyDrive(request).getBody();
        List<EWasteDrive> eWasteDriveList1 = (List<EWasteDrive>) envelopeMessage.getData();
        assertEquals(eWasteDrive.getCollectorEmail(),eWasteDriveList1.get(0).getCollectorEmail());
    }

    @Test
    void viewMyDrive_Failure(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn(collector);

        List<EWasteDrive> eWasteDriveList2 = new ArrayList<>();
        when(eWasteDriveRepo.getAllByCollectorEmail(collector.getEmail())).thenReturn(eWasteDriveList2);
        EnvelopeMessage envelopeMessage1 = (EnvelopeMessage) collectorService.viewMyDrive(request).getBody();
        assertEquals("Enter all Details",envelopeMessage1.getData());
    }

    @Test
    void viewProfile_Success(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn(collector);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.viewProfile(request).getBody();
        Collector c=(Collector) envelopeMessage.getData();
        assertEquals(collector.getEmail(), c.getEmail());
    }

    @Test
    void viewProfile_Failure(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((null));
        EnvelopeMessage envelopeMessage1 = (EnvelopeMessage) collectorService.viewProfile(request).getBody();
        assertEquals("No Such User Found", envelopeMessage1.getData());

    }

    @Test
    void editProfile_Success() {
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

        Collector collector= new Collector();
        collector.setFirstName(userModel.getFirstName());
        collector.setLastName(userModel.getLastName());
        collector.setEmail(userModel.getEmail());
        collector.setCity(userModel.getCity());
        collector.setState(userModel.getState());
        collector.setAddress1(userModel.getAddress1());
        collector.setMobileNo(userModel.getMobileNo());
        collector.setPassword(userModel.getPassword());
        collector.setPinCode(userModel.getPinCode());
        collector.setUid(collector.getUid());


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        EnvelopeMessage envelopeMessage=(EnvelopeMessage) collectorService.editProfile(userModel,request).getBody();
        Collector c=(Collector) envelopeMessage.getData();
        assertEquals(collector.getEmail(), c.getEmail());
    }

    @Test
    void editProfile_Failure() {
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

        Collector collector= new Collector();
        collector.setFirstName(userModel.getFirstName());
        collector.setLastName(userModel.getLastName());
        collector.setEmail(userModel.getEmail());
        collector.setCity(userModel.getCity());
        collector.setState(userModel.getState());
        collector.setAddress1(userModel.getAddress1());
        collector.setMobileNo(userModel.getMobileNo());
        collector.setPassword(userModel.getPassword());
        collector.setPinCode(userModel.getPinCode());
        collector.setUid(collector.getUid());


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((null));
        EnvelopeMessage envelopeMessage1=(EnvelopeMessage) collectorService.editProfile(userModel,request).getBody();
        assertEquals("No Such User Found", envelopeMessage1.getData());
    }

    @Test
    void sellItemAvailableSummary_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        List<SellItems> sellItemsList = new ArrayList<>();
        sellItemsList.add(sellItems);
        when(sellItemRepo.findAllByCollectorUidAndStatus(collector.getUid(),AVAILABLE)).thenReturn(sellItemsList);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.sellItemAvailableSummary(request).getBody();
        List<SellItems> itemsList = (List<SellItems>) envelopeMessage.getData();
        assertEquals(sellItemsList.get(0).getCollectorUid(), itemsList.get(0).getCollectorUid());

    }

    @Test
    void sellItemAvailableSummary_Failure() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));
        List<SellItems> sellItemsList1 = new ArrayList<>();
        when(sellItemRepo.findAllByCollectorUidAndStatus(collector.getUid(),AVAILABLE)).thenReturn(sellItemsList1);
        EnvelopeMessage envelopeMessage1 = (EnvelopeMessage) collectorService.sellItemAvailableSummary(request).getBody();

        assertEquals("No items in Sale",envelopeMessage1.getData());
    }

    @Test
    void sellItemSoldSummary_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        List<VendorOrders>vendorOrdersList=new ArrayList<>();
        vendorOrdersList.add(vendorOrders);
        when(vendorOrdersRepo.findAllByCollectorUid(collector.getUid())).thenReturn(vendorOrdersList);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.sellItemSoldSummary(request).getBody();
        List<VendorOrders> itemsList = (List<VendorOrders>) envelopeMessage.getData();
        assertEquals(vendorOrdersList.get(0).getCollectorUid(), itemsList.get(0).getCollectorUid());
    }

    @Test
    void sellItemSoldSummary_Failure() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        List<VendorOrders>vendorOrdersList=new ArrayList<>();
        when(vendorOrdersRepo.findAllByCollectorUid(collector.getUid())).thenReturn(vendorOrdersList);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.sellItemSoldSummary(request).getBody();

        assertEquals("No items in Sale",envelopeMessage.getData());
    }


    @Test
    void viewCustomerProfile_Success() {
        when(customerRepo.findByUid(customer.getUid())).thenReturn(customer);
        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        EnvelopeMessage envelopeMessage= (EnvelopeMessage) collectorService.viewCustomerProfile(customer.getUid()).getBody();
        Customer customer1=(Customer) envelopeMessage.getData();
        assertEquals(customer.getUid(),customer1.getUid());
    }

    @Test
    void viewCustomerProfile_Failure() {
        when(customerRepo.findByUid(customer.getUid())).thenReturn(null);
        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        EnvelopeMessage envelopeMessage1= (EnvelopeMessage) collectorService.viewCustomerProfile(customer.getUid()).getBody();
        assertEquals("Profile Not Found",envelopeMessage1.getData());
    }
    @Test
    void viewNotification_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        Notification customerNotification=new Notification();
        customerNotification.setCustomerUid(collector.getUid());
        customerNotification.setRole("Collector");
        customerNotification.setMessage("TEST Message");
        customerNotification.setStatus(false);

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(customerNotification);
        when(notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid())).thenReturn(notificationList);

        EnvelopeMessage envelopeMessage=(EnvelopeMessage) collectorService.viewNotification(request).getBody();
        List<Notification>customerNotification1= (List<Notification>) envelopeMessage.getData();

        assertEquals(notificationList.get(0).getCollectorUid(),customerNotification1.get(0).getCollectorUid());
    }

    @Test
    void viewNotification_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        List<Notification> notificationList = new ArrayList<>();
        when(notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid())).thenReturn(notificationList);

        EnvelopeMessage envelopeMessage=(EnvelopeMessage) collectorService.viewNotification(request).getBody();

        assertEquals(NO_NEW_NOTIFICATION,envelopeMessage.getData());
    }

    @Test
    void readNotification_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        Notification customerNotification=new Notification();
        customerNotification.setCustomerUid(collector.getUid());
        customerNotification.setRole("Collector");
        customerNotification.setMessage("TEST Message");
        customerNotification.setStatus(false);

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(customerNotification);
        when(notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid())).thenReturn(notificationList);

        EnvelopeMessage envelopeMessage=(EnvelopeMessage) collectorService.readNotification(request).getBody();
        List<Notification>customerNotification1= (List<Notification>) envelopeMessage.getData();

        assertEquals(notificationList.get(0).getCollectorUid(),customerNotification1.get(0).getCollectorUid());
    }

    @Test
    void readNotification_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        List<Notification> notificationList = new ArrayList<>();
        when(notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid())).thenReturn(notificationList);

        EnvelopeMessage envelopeMessage=(EnvelopeMessage) collectorService.readNotification(request).getBody();

        assertEquals(NO_NEW_NOTIFICATION,envelopeMessage.getData());
    }
    @Test
    void editDriveSummary_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        when(eWasteDriveRepo.findByCollectorEmailAndId(collector.getEmail(),eWasteDrive.getId())).thenReturn(eWasteDrive);
        EnvelopeMessage envelopeMessage=(EnvelopeMessage) collectorService.editDriveSummary(eWasteDrive.getId(),"upcoming",request).getBody();
        EWasteDrive eWasteDrive1= (EWasteDrive) envelopeMessage.getData();
        assertEquals(eWasteDrive.getCollectorEmail(),eWasteDrive1.getCollectorEmail());
    }

    @Test
    void editDriveSummary_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        when(eWasteDriveRepo.findByCollectorEmailAndId(collector.getEmail(),eWasteDrive.getId())).thenReturn(null);
        EnvelopeMessage envelopeMessage1=(EnvelopeMessage) collectorService.editDriveSummary(eWasteDrive.getId(),"upcoming",request).getBody();
        assertEquals("Drive Not Found",envelopeMessage1.getData());
    }

    @Test
    void getAnalytics()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        List<EWasteDrive>listEWasteDriveInCity= new ArrayList<>();
        listEWasteDriveInCity.add(eWasteDrive);
        listEWasteDriveInCity.add(eWasteDrive);
        when(eWasteDriveRepo.findAllByCity(collector.getCity())).thenReturn(listEWasteDriveInCity);

        List<EWasteDrive>listOfEWasteDrive= new ArrayList<>();
        listOfEWasteDrive.add(eWasteDrive);
        when(eWasteDriveRepo.getAllByCollectorEmail(collector.getEmail())).thenReturn(listOfEWasteDrive);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("EWasteDriveCollector",listOfEWasteDrive.size());
        hashMap.put("EWasteDriveCity",listEWasteDriveInCity.size());

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.getAnalytics(request).getBody();
        HashMap<String,Integer> hashMap1 = (HashMap) envelopeMessage.getData();

        assertEquals(hashMap.get("EWasteDriveCity"),hashMap1.get("EWasteDriveCity"));

    }

    @Test
    void getAnalyticsV2()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        List<Collector> collectorList = new ArrayList<>();
        collectorList.add(collector);
        when(collectorRepo.findByCity(collector.getCity())).thenReturn(collectorList);

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
        hashMap.put(LAPMS_COLLECTED,listLapmsCollected.size());
        hashMap.put(LARGE_EQIP_COLLECTED,listLargeEqipCollected.size());
        hashMap.put(SMALL_EQUIP_COLLECTED,listSmallEquipCollected.size());
        hashMap.put(SMALL_IT_COLLECTED,listSmallITCollected.size());
        hashMap.put(SCREENS_COLLECTED,listScreensCollected.size());

        EnvelopeMessage envelopeMessage=(EnvelopeMessage)collectorService.getAnalyticsV2(request).getBody();
        HashMap<String,Integer>hm= (HashMap<String, Integer>) envelopeMessage.getData();

        assertEquals(hashMap.get(TEMP_COLLECTED),hm.get(TEMP_COLLECTED));
    }

    @Test
    void getAnalyticsV4()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        List<Vendor>listVendorInCity= new ArrayList<>();
        listVendorInCity.add(vendor);
        when(vendorRepo.findByCity(collector.getCity())).thenReturn(listVendorInCity);

        List<Vendor>listOfVendorAll= new ArrayList<>();
        listOfVendorAll.add(vendor);
        listOfVendorAll.add(vendor);
        when(vendorRepo.findAll()).thenReturn(listOfVendorAll);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("vendorCity",listVendorInCity.size());
        hashMap.put("vendorAllCity",listOfVendorAll.size());

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.getAnalyticsV4(request).getBody();
        HashMap<String,Integer> hashMap1 = (HashMap) envelopeMessage.getData();

        assertEquals(hashMap.get("vendorCity"),hashMap1.get("vendorCity"));

    }

    @Test
    void getAnalyticsV5()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", collector.getEmail());

        when(collectorRepo.findByEmail(collector.getEmail())).thenReturn((collector));

        List<Customer>listCustomerInCity= new ArrayList<>();
        listCustomerInCity.add(customer);
        when(customerRepo.findByCity(collector.getCity())).thenReturn(listCustomerInCity);

        List<Customer>listCustomerAll= new ArrayList<>();
        listCustomerAll.add(customer);
        listCustomerAll.add(customer);
        when(customerRepo.findAll()).thenReturn(listCustomerAll);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("customerCity",listCustomerInCity.size());
        hashMap.put("customerAllCity",listCustomerAll.size());

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) collectorService.getAnalyticsV5(request).getBody();
        HashMap<String,Integer> hashMap1 = (HashMap) envelopeMessage.getData();

        assertEquals(hashMap.get("customerCity"),hashMap1.get("customerCity"));
    }
}
