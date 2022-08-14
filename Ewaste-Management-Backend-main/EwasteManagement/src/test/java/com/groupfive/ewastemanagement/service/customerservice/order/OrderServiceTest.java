package com.groupfive.ewastemanagement.service.customerservice.order;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.collectorentity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.collectorentity.EWasteDrive;
import com.groupfive.ewastemanagement.entity.collectorentity.EWasteDriveCategories;
import com.groupfive.ewastemanagement.entity.customerentity.Customer;
import com.groupfive.ewastemanagement.entity.customerentity.Orders;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.model.RequestModel;
import com.groupfive.ewastemanagement.repository.collectorrepository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.EWasteDriveRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.CustomerRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.OrdersRepo;
import com.groupfive.ewastemanagement.service.customerservice.CustomerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

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
    void createPickUpRequest() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());


        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));
        when(ordersRepo.save(orders)).thenReturn(orders);

        List<Collector> list = new ArrayList<>();
        list.add(collector);
        when(collectorRepo.findByCity(customer.getCity())).thenReturn(list);
        List<String> stringList = new ArrayList<>();
        stringList.add(collector.getUid());

        when(categoriesAcceptedRepo.findByCategoryAccepted(orders.getCategory())).thenReturn(stringList);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) orderService.createPickUpRequest(requestModel, request).getBody();

        Orders orders1 = (Orders) envelopeMessage.getData();
        //changed
        assertEquals(orders.getCustomerUid(), orders1.getCustomerUid());
    }

    @Test
    void showDropOffLocation() {

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        List<Collector> list = new ArrayList<>();
        list.add(collector);
        when(collectorRepo.findByCity(customer.getCity())).thenReturn(list);

        List<String> stringList = new ArrayList<>();
        stringList.add(collector.getUid());
        when(categoriesAcceptedRepo.findByCategoryAccepted("Laptop")).thenReturn(stringList);

        //changed
        when(collectorRepo.findByUid(stringList.get(0))).thenReturn(collector);

        assertEquals(1, orderService.showDropOffLocation("Laptop", request));
    }

    @Test
    void viewListOfCollectorsForDropOff() {

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        List<Collector> list = new ArrayList<>();
        list.add(collector);
        when(collectorRepo.findByCity(customer.getCity())).thenReturn(list);

        List<String> stringList = new ArrayList<>();
        stringList.add(collector.getUid());
        when(categoriesAcceptedRepo.findByCategoryAccepted("Laptop")).thenReturn(stringList);

        //changed
        when(collectorRepo.findByUid(stringList.get(0))).thenReturn(collector);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) orderService.viewListOfCollectorsForDropOff("Laptop", request).getBody();

        List<Collector> listOfCollectors = (List<Collector>) envelopeMessage.getData();
        assertEquals(collector.getUid(), listOfCollectors.get(0).getUid());
    }

    @Test
    void countCollectorPickUp() {

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        List<Collector> list = new ArrayList<>();
        list.add(collector);
        when(collectorRepo.findByCity(customer.getCity())).thenReturn(list);

        List<String> stringList = new ArrayList<>();
        stringList.add(collector.getUid());
        when(categoriesAcceptedRepo.findByCategoryAccepted("IT Appliances")).thenReturn(stringList);

        when(collectorRepo.findByUid(stringList.get(0))).thenReturn(collector);

        assertEquals(1, orderService.countCollectorPickUp(("IT Appliances"), request));
    }

    @Test
    void createDropOffRequest() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", customer.getEmail());

        when(customerRepo.findByEmail(customer.getEmail())).thenReturn((customer));
        when(ordersRepo.save(orders)).thenReturn(orders);

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) orderService.createDropOffRequest(requestModel, request).getBody();

        Orders serviceOrders = (Orders) envelopeMessage.getData();
        assertEquals(orders.getCollectorUid(), serviceOrders.getCollectorUid());

    }
}
