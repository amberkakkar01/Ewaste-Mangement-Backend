package com.groupfive.ewastemanagement.service.vendorservice;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.collectorentity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.collectorentity.SellItems;
import com.groupfive.ewastemanagement.entity.vendorentity.Vendor;
import com.groupfive.ewastemanagement.entity.vendorentity.VendorOrders;
import com.groupfive.ewastemanagement.model.AcceptSellItemsVendor;
import com.groupfive.ewastemanagement.model.SellItemModel;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.repository.collectorrepository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.SellItemRepo;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static com.groupfive.ewastemanagement.service.vendorservice.VendorServiceImplementation.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VendorServiceImplementationTest {

    @MockBean
    private VendorRepo vendorRepo;

    @MockBean
    private SellItemRepo sellItemRepo;

    @Autowired
    private VendorService vendorService;

    @MockBean
    private VendorOrdersRepo vendorOrdersRepo;

    @MockBean
    private CollectorRepo collectorRepo;

    @MockBean
    private CategoriesAcceptedRepo categoriesAcceptedRepo;

    Collector collector;
    Vendor vendor;
    AcceptSellItemsVendor acceptSellItemsVendor;
    SellItemModel sellItemModel;
    SellItems sellItems;
    VendorOrders vendorOrders;
    @BeforeAll
    public void setUp()
    {
        collector = new Collector();
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

        acceptSellItemsVendor=new AcceptSellItemsVendor();
        acceptSellItemsVendor.setId(1L);
        acceptSellItemsVendor.setDate("2022-08-22");
        acceptSellItemsVendor.setPrice("1000");
        acceptSellItemsVendor.setQuantity("12");

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
        sellItems.setCollectorUid(collector.getUid());
        sellItems.setCategory(sellItemModel.getCategory());
        sellItems.setAvailableQuantity(sellItemModel.getQuantity());

        vendorOrders = new VendorOrders();
        vendorOrders.setCollectorUid(collector.getUid());
        vendorOrders.setItemName("ASAS");
        vendorOrders.setCategory("CAT");
        vendorOrders.setQuantity("9");
        vendorOrders.setStatus("completed");
        vendorOrders.setDate("2022-03-30");
        vendorOrders.setPrice("200");
        vendorOrders.setAddress("XYZ");
        vendorOrders.setVendorUid(vendor.getUid());
        vendorOrders.setId(1L);
    }


    @Test
    void viewProfile_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());
        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn((vendor));
        EnvelopeMessage envelopeMessage=(EnvelopeMessage)vendorService.viewProfile(request).getBody();
        Vendor c=(Vendor) envelopeMessage.getData();
        assertEquals(vendor.getEmail(), c.getEmail());
    }

    @Test
    void viewProfile_Failure() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());
        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn((null));
        EnvelopeMessage envelopeMessage1 = (EnvelopeMessage) vendorService.viewProfile(request).getBody();
        assertEquals("No Such User Found", envelopeMessage1.getData());
    }

    @Test
    void viewAccept() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn(vendor);

        when(sellItemRepo.findByIdAndStatus(1L,"Available")).thenReturn(sellItems);

        when(collectorRepo.findByUid(sellItems.getCollectorUid())).thenReturn(collector);

        EnvelopeMessage envelopeMessage= (EnvelopeMessage) vendorService.viewAccept(acceptSellItemsVendor,request).getBody();

        VendorOrders vendorOrders= (VendorOrders) envelopeMessage.getData();

        assertEquals(sellItems.getCollectorUid(),vendorOrders.getCollectorUid());
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

        Vendor vendor= new Vendor();
        vendor.setFirstName(userModel.getFirstName());
        vendor.setLastName(userModel.getLastName());
        vendor.setEmail(userModel.getEmail());
        vendor.setCity(userModel.getCity());
        vendor.setState(userModel.getState());
        vendor.setAddress1(userModel.getAddress1());
        vendor.setMobileNo(userModel.getMobileNo());
        vendor.setPassword(userModel.getPassword());
        vendor.setPinCode(userModel.getPinCode());
        vendor.setUid(vendor.getUid());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn((vendor));
        EnvelopeMessage envelopeMessage=(EnvelopeMessage)vendorService.editProfile(userModel,request).getBody();
        Vendor c=(Vendor) envelopeMessage.getData();
        assertEquals(vendor.getEmail(), c.getEmail());
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

        Vendor vendor= new Vendor();
        vendor.setFirstName(userModel.getFirstName());
        vendor.setLastName(userModel.getLastName());
        vendor.setEmail(userModel.getEmail());
        vendor.setCity(userModel.getCity());
        vendor.setState(userModel.getState());
        vendor.setAddress1(userModel.getAddress1());
        vendor.setMobileNo(userModel.getMobileNo());
        vendor.setPassword(userModel.getPassword());
        vendor.setPinCode(userModel.getPinCode());
        vendor.setUid(vendor.getUid());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn((null));
        EnvelopeMessage envelopeMessage1=(EnvelopeMessage)vendorService.editProfile(userModel,request).getBody();
        assertEquals("No Such User Found", envelopeMessage1.getData());
    }

    @Test
    void viewAllItemOnSale_Success() {

        List<SellItems> sellItemsList = new ArrayList<>();
        sellItemsList.add(sellItems);
        when(sellItemRepo.findAllByStatus("Available")).thenReturn(sellItemsList);
        EnvelopeMessage envelopeMessage = (EnvelopeMessage) vendorService.viewAllItemOnSale().getBody();
        List<SellItems> sellItemsList1 = (List<SellItems>) envelopeMessage.getData();
        assertEquals(sellItemsList.get(0).getCollectorUid(),sellItemsList1.get(0).getCollectorUid());
    }

    @Test
    void viewAllItemOnSale_Failure() {

        List<SellItems> sellItemsList = new ArrayList<>();
        sellItemsList.add(sellItems);

        when(sellItemRepo.findAllByStatus("Available")).thenReturn(new ArrayList<>());
        EnvelopeMessage envelopeMessage2 = (EnvelopeMessage) vendorService.viewAllItemOnSale().getBody();
        assertEquals("No Item Found",envelopeMessage2.getData());
    }

    @Test
    void orderSummary_Success() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn((vendor));

        List<VendorOrders> vendorOrdersList =  new ArrayList<>();
        vendorOrdersList.add(vendorOrders);
        when(vendorOrdersRepo.findAllByVendorUid(vendor.getUid())).thenReturn(vendorOrdersList);
        EnvelopeMessage envelopeMessage= (EnvelopeMessage) vendorService.orderSummary(request).getBody();
        List<VendorOrders> vendorOrdersList1= (List<VendorOrders>) envelopeMessage.getData();
        assertEquals(vendorOrdersList.get(0).getCollectorUid(),vendorOrdersList1.get(0).getCollectorUid());
    }

    @Test
    void orderSummary_Failure() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn((vendor));

        List<VendorOrders> vendorOrdersList =  new ArrayList<>();
        vendorOrdersList.add(vendorOrders);

        when(vendorOrdersRepo.findAllByVendorUid(vendor.getUid())).thenReturn(new ArrayList<>());
        EnvelopeMessage envelopeMessage2= (EnvelopeMessage) vendorService.orderSummary(request).getBody();
        assertEquals("No such order exist",envelopeMessage2.getData());
    }

    @Test
    void viewCollectorProfileSummary_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());


        when(vendorOrdersRepo.findById(1L)).thenReturn(Optional.of(vendorOrders));
        when(collectorRepo.findByUid(vendorOrders.getCollectorUid())).thenReturn(collector);
        EnvelopeMessage envelopeMessage=(EnvelopeMessage)vendorService.viewCollectorProfileInSummary(vendorOrders.getId(), request).getBody();
        Collector collector1=(Collector) envelopeMessage.getData();
        assertEquals(vendorOrders.getCollectorUid(),collector1.getUid());
    }

    @Test
    void viewCollectorProfileSummary_Failure() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorOrdersRepo.findById(1L)).thenReturn(Optional.of(vendorOrders));
        when(collectorRepo.findByUid(vendorOrders.getCollectorUid())).thenReturn(null);
        EnvelopeMessage envelopeMessage2=(EnvelopeMessage)vendorService.viewCollectorProfileInSummary(vendorOrders.getId(), request).getBody();
        assertEquals("No Collector Found", envelopeMessage2.getData());
    }

    @Test
    void viewCollectorProfile_Success()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());


        when(sellItemRepo.findById(1L)).thenReturn(Optional.of(sellItems));
        when(collectorRepo.findByUid(sellItems.getCollectorUid())).thenReturn(collector);
        EnvelopeMessage envelopeMessage=(EnvelopeMessage)vendorService.viewCollectorProfile(sellItems.getId(), request).getBody();
        Collector collector1=(Collector) envelopeMessage.getData();
        assertEquals(sellItems.getCollectorUid(),collector1.getUid());

    }

    @Test
    void viewCollectorProfile_Failure()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(sellItemRepo.findById(1L)).thenReturn(Optional.of(sellItems));

        when(collectorRepo.findByUid(sellItems.getCollectorUid())).thenReturn(null);
        EnvelopeMessage envelopeMessage2=(EnvelopeMessage)vendorService.viewCollectorProfile(sellItems.getId(), request).getBody();
        assertEquals("No Collector Found",envelopeMessage2.getData());
    }

    @Test
    void getAnalytics()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn((vendor));
        List<Vendor>listVendorInCity= new ArrayList<>();
        listVendorInCity.add(vendor);
        when(vendorRepo.findByCity(collector.getCity())).thenReturn(listVendorInCity);

        List<Vendor>listOfVendorAll= new ArrayList<>();
        listOfVendorAll.add(vendor);
        listOfVendorAll.add(vendor);
        when(vendorRepo.findAll()).thenReturn(listOfVendorAll);

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("vendorInCity",listVendorInCity.size());
        hashMap.put("allVendor",listOfVendorAll.size());


        List<Collector> collectorList = new ArrayList<>();
        collectorList.add(collector);
        when(collectorRepo.findByCity(vendor.getCity())).thenReturn(collectorList);

        List<Collector> allCollectorList = new ArrayList<>();
        allCollectorList.add(collector);
        when(collectorRepo.findAll()).thenReturn(allCollectorList);
        hashMap.put("collectorInCity",collectorList.size());
        hashMap.put("allCollector",allCollectorList.size());

        EnvelopeMessage envelopeMessage = (EnvelopeMessage) vendorService.getAnalytics(request).getBody();
        HashMap<String,Integer> hashMap1 = (HashMap) envelopeMessage.getData();

        assertEquals(hashMap.get("vendorInCity"),hashMap1.get("vendorInCity"));
        assertEquals(hashMap.get("collectorInCity"),hashMap1.get("collectorInCity"));

    }

    @Test
    void getAnalyticsV2()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn(vendor);

        List<Collector> collectorList = new ArrayList<>();
        collectorList.add(collector);
        when(collectorRepo.findByCity(vendor.getCity())).thenReturn(collectorList);

        List<SellItems> sellItemsListTemp = new ArrayList<>();
        sellItemsListTemp.add(sellItems);
        when(sellItemRepo.findByCategoryAndCollectorUid(TEMP, collector.getUid())).thenReturn(sellItemsListTemp);

        EnvelopeMessage envelopeMessage=vendorService.getAnalyticsV2(request).getBody();
        HashMap<String,Integer>hashMap= (HashMap<String, Integer>) envelopeMessage.getData();
        assertEquals(1,hashMap.get(TEMP_COLLECTOR_SALE));
    }

    @Test
    void getAnalyticsV4(){
        List<Collector> collectorList = new ArrayList<>();
        collectorList.add(collector);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("EMAIL", vendor.getEmail());

        when(vendorRepo.findByEmail(vendor.getEmail())).thenReturn(vendor);


        when(collectorRepo.findByCity(vendor.getCity())).thenReturn(collectorList);

        List<CategoriesAccepted> list1 = new ArrayList<>();
        CategoriesAccepted categoriesAccepted=new CategoriesAccepted();
        categoriesAccepted.setCategoryAccepted(TEMP);
        list1.add(categoriesAccepted);
        when(categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, collector.getId())).thenReturn(list1);

        EnvelopeMessage envelopeMessage=(EnvelopeMessage) vendorService.getAnalyticsV4(request).getBody();
        HashMap<String,Integer>hashMap= (HashMap<String, Integer>) envelopeMessage.getData();

        assertEquals(1,hashMap.get(TEMP_CITY));

    }

}