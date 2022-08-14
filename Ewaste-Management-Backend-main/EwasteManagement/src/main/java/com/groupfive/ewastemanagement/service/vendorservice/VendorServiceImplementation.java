package com.groupfive.ewastemanagement.service.vendorservice;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.collectorentity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.collectorentity.SellItems;
import com.groupfive.ewastemanagement.entity.notification.Notification;
import com.groupfive.ewastemanagement.entity.vendorentity.Vendor;
import com.groupfive.ewastemanagement.entity.vendorentity.VendorOrders;
import com.groupfive.ewastemanagement.model.AcceptSellItemsVendor;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.repository.collectorrepository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.SellItemRepo;
import com.groupfive.ewastemanagement.repository.notification.NotificationRepo;
import com.groupfive.ewastemanagement.repository.vendorrepository.VendorOrdersRepo;
import com.groupfive.ewastemanagement.repository.vendorrepository.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.groupfive.ewastemanagement.helper.Constants.*;

@Service
public class VendorServiceImplementation implements VendorService{

    @Autowired
    private SellItemRepo sellItemRepo;

    @Autowired
    private VendorRepo vendorRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VendorOrdersRepo vendorOrdersRepo;

    @Autowired
    private CollectorRepo collectorRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private CategoriesAcceptedRepo categoriesAcceptedRepo;

    @Override
    public ResponseEntity<EnvelopeMessage> viewProfile(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);
        Vendor vendor=vendorRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (vendor==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_SUCH_USER_FOUND)    ;

            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(vendor);

        return new ResponseEntity<>(envelopeMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewAccept(AcceptSellItemsVendor acceptSellItemsVendor, HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);

        Vendor vendor=vendorRepo.findByEmail(email);
        Long id=acceptSellItemsVendor.getId();
        int quantity= Integer.parseInt(acceptSellItemsVendor.getQuantity());

        SellItems sellItems = sellItemRepo.findByIdAndStatus(id, AVAILABLE);

        EnvelopeMessage envelopeMessage = new EnvelopeMessage();

        VendorOrders vendorOrders = new VendorOrders();
        int quantityCollector= Integer.parseInt(sellItems.getAvailableQuantity());

        if (quantity>quantityCollector)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(REDUCE_QUANTITY);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        else if (quantityCollector==0)
        {
            sellItems.setStatus(SOLD);
            sellItemRepo.save(sellItems);

            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(sellItems);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
        }
        else {
            quantityCollector=quantityCollector-quantity;

            sellItems.setAvailableQuantity(String.valueOf(quantityCollector));

            vendorOrders.setItemName(sellItems.getItemName());
            vendorOrders.setCategory(sellItems.getCategory());
            vendorOrders.setVendor(vendor);
            vendorOrders.setCollector(sellItems.getCollector());

            vendorOrders.setVendorUid(vendor.getUid());
            vendorOrders.setCollectorUid(sellItems.getCollectorUid());

            vendorOrders.setDate(acceptSellItemsVendor.getDate());

            Collector collector=collectorRepo.findByUid(sellItems.getCollectorUid());
            String address=collector.getAddress1()+" "+collector.getCity()+" "+collector.getState();

            vendorOrders.setAddress(address);
            vendorOrders.setQuantity(String.valueOf(quantity));
            vendorOrders.setStatus(COMPLETED);
            vendorOrders.setPrice(acceptSellItemsVendor.getPrice());

            Notification vendorNotification = new Notification();
            vendorNotification.setCollectorUid(sellItems.getCollectorUid());
            vendorNotification.setCollector(collector);
            vendorNotification.setRole("Collector");
            vendorNotification.setStatus(false);
            vendorNotification.setMessage("Your "+ vendorOrders.getItemName() +" is purchased by the vendor");

            notificationRepo.save(vendorNotification);
            vendorOrdersRepo.save(vendorOrders);
        }
        sellItemRepo.save(sellItems);

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(vendorOrders);

        return new ResponseEntity<>(envelopeMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> editProfile(UserModel userModel, HttpServletRequest request) {

        String email=request.getHeader(EMAIL);

        Vendor vendor=vendorRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (vendor==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_SUCH_USER_FOUND);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        vendor.setEmail(userModel.getEmail());
        vendor.setFirstName(userModel.getFirstName());
        vendor.setLastName(userModel.getLastName());
        vendor.setPassword(passwordEncoder.encode(userModel.getPassword()));
        vendor.setMobileNo(userModel.getMobileNo());

        vendor.setUid(vendor.getUid());

        vendor.setAddress1(userModel.getAddress1());
        vendor.setCity(userModel.getCity());
        vendor.setState(userModel.getState());
        vendor.setPinCode(userModel.getPinCode());

        vendor.setGstNo(userModel.getGstNo());
        vendor.setRegistrationNo(userModel.getRegistrationNo());

        vendorRepo.save(vendor);

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(vendor);

        return new ResponseEntity<>(envelopeMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewAllItemOnSale() {

        List<SellItems> sellItems=sellItemRepo.findAllByStatus(AVAILABLE);


        for (SellItems items : sellItems) {
            if (items.getAvailableQuantity().equals("0")) {
                items.setStatus(OUT_OF_STOCK);
                sellItemRepo.save(items);
            }
        }
        sellItems=sellItemRepo.findAllByStatus(AVAILABLE);

        EnvelopeMessage envelopeMessage = new EnvelopeMessage();
        if (sellItems.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_ITEM_FOUND);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(sellItems);

        return new ResponseEntity<>(envelopeMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> orderSummary(HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);

        Vendor vendor=vendorRepo.findByEmail(email);
        List<VendorOrders> vendorOrdersList = vendorOrdersRepo.findAllByVendorUid(vendor.getUid());

        EnvelopeMessage envelopeMessage = new EnvelopeMessage();
        if (vendorOrdersList.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_SUCH_ORDER_EXIST);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(vendorOrdersList);

        return new ResponseEntity<>(envelopeMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewCollectorProfileInSummary(Long id, HttpServletRequest request)
    {
        Optional<VendorOrders> vendorOrders = vendorOrdersRepo.findById(id);
        Collector collector=new Collector();
        if (vendorOrders.isPresent()) {
            collector = collectorRepo.findByUid(vendorOrders.get().getCollectorUid());
        }
        EnvelopeMessage envelopeMessage = new EnvelopeMessage();
        if (collector==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_COLLECTOR_FOUND);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(collector);

        return new ResponseEntity<>(envelopeMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewCollectorProfile(Long id, HttpServletRequest request) {
        Optional<SellItems> sellItemsList=sellItemRepo.findById(id);

        Collector collector=new Collector();
        if (sellItemsList.isPresent()) {
            collector = collectorRepo.findByUid(sellItemsList.get().getCollectorUid());
        }
        EnvelopeMessage envelopeMessage = new EnvelopeMessage();
        if (collector==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_COLLECTOR_FOUND);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(collector);

        return new ResponseEntity<>(envelopeMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAnalytics(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);

        Vendor vendor=vendorRepo.findByEmail(email);

        HashMap<String,Integer> hashMap = new HashMap<>();
        List<Vendor> vendorList = vendorRepo.findByCity(vendor.getCity());
        List<Vendor> allVendorList = vendorRepo.findAll();

        hashMap.put(VENDOR_IN_CITY,vendorList.size());
        hashMap.put(ALL_VENDOR,allVendorList.size());

        List<Collector> collectorList = collectorRepo.findByCity(vendor.getCity());
        List<Collector> allCollectorList = collectorRepo.findAll();

        hashMap.put(COLLECTOR_IN_CITY,collectorList.size());
        hashMap.put(ALL_COLLECTOR,allCollectorList.size());

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(hashMap);
        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAnalyticsV2(HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);
        Vendor vendor=vendorRepo.findByEmail(email);

        HashMap<String,Integer> hashMap = new HashMap<>();

        hashMap.put(TEMP_COLLECTOR_SALE,0);
        hashMap.put(LAPMS_COLLECTOR_SALE,0);
        hashMap.put(LARGE_EQIP_COLLECTOR_SALE,0);
        hashMap.put(SMALL_EQUIP_COLLECTOR_SALE,0);
        hashMap.put(SMALL_IT_COLLECTOR_SALE,0);
        hashMap.put(SCREENS_COLLECTOR_SALE,0);

        hashMap.put(TEMP_VENDOR,0);
        hashMap.put(LAPMS_VENDOR,0);
        hashMap.put(LARGE_EQIP_VENDOR,0);
        hashMap.put(SMALL_EQUIP_VENDOR,0);
        hashMap.put(SMALL_IT_VENDOR,0);
        hashMap.put(SCREENS_VENDOR,0);

        List<Collector> collectorList = collectorRepo.findByCity(vendor.getCity());
        for (Collector collector : collectorList) {
            List<SellItems> sellItemsListTemp = sellItemRepo.findByCategoryAndCollectorUid(TEMP, collector.getUid());
            hashMap.put(TEMP_COLLECTOR_SALE, hashMap.get(TEMP_COLLECTOR_SALE) + sellItemsListTemp.size());

            List<SellItems> sellItemsListLamp = sellItemRepo.findByCategoryAndCollectorUid(LAPMS, collector.getUid());
            hashMap.put(LAPMS_COLLECTOR_SALE, hashMap.get(LAPMS_COLLECTOR_SALE) + sellItemsListLamp.size());

            List<SellItems> sellItemsListLargeEquip = sellItemRepo.findByCategoryAndCollectorUid(LARGE_EQIP, collector.getUid());
            hashMap.put(LARGE_EQIP_COLLECTOR_SALE, hashMap.get(LARGE_EQIP_COLLECTOR_SALE) + sellItemsListLargeEquip.size());

            List<SellItems> sellItemsListSmallEquip = sellItemRepo.findByCategoryAndCollectorUid(SMALL_EQUIP, collector.getUid());
            hashMap.put(SMALL_EQUIP_COLLECTOR_SALE, hashMap.get(SMALL_EQUIP_COLLECTOR_SALE) + sellItemsListSmallEquip.size());

            List<SellItems> sellItemsListSmallIT = sellItemRepo.findByCategoryAndCollectorUid(SMALL_IT, collector.getUid());
            hashMap.put(SMALL_IT_COLLECTOR_SALE, hashMap.get(SMALL_IT_COLLECTOR_SALE) + sellItemsListSmallIT.size());

            List<SellItems> sellItemsListScreens = sellItemRepo.findByCategoryAndCollectorUid(SCREENS, collector.getUid());
            hashMap.put(SCREENS_COLLECTOR_SALE, hashMap.get(SCREENS_COLLECTOR_SALE) + sellItemsListScreens.size());

            List<VendorOrders> vendorOrdersList1 = vendorOrdersRepo.findByCategoryAndCollectorUid(TEMP, collector.getUid());
            hashMap.put(TEMP_VENDOR, hashMap.get(TEMP_VENDOR) + vendorOrdersList1.size());

            List<VendorOrders> vendorOrdersList2 = vendorOrdersRepo.findByCategoryAndCollectorUid(LAPMS, collector.getUid());
            hashMap.put(LAPMS_VENDOR, hashMap.get(LAPMS_VENDOR) + vendorOrdersList2.size());

            List<VendorOrders> vendorOrdersList3 = vendorOrdersRepo.findByCategoryAndCollectorUid(LARGE_EQIP, collector.getUid());
            hashMap.put(LARGE_EQIP_VENDOR, hashMap.get(LARGE_EQIP_VENDOR) + vendorOrdersList3.size());

            List<VendorOrders> vendorOrdersList4 = vendorOrdersRepo.findByCategoryAndCollectorUid(SMALL_EQUIP, collector.getUid());
            hashMap.put(SMALL_EQUIP_VENDOR,hashMap.get(SMALL_EQUIP_VENDOR) + vendorOrdersList4.size());

            List<VendorOrders> vendorOrdersList5 = vendorOrdersRepo.findByCategoryAndCollectorUid(SMALL_IT, collector.getUid());
            hashMap.put(SMALL_IT_VENDOR, hashMap.get(SMALL_IT_VENDOR) + vendorOrdersList5.size());

            List<VendorOrders> vendorOrdersList6 = vendorOrdersRepo.findByCategoryAndCollectorUid(SCREENS, collector.getUid());
            hashMap.put(SCREENS_VENDOR, hashMap.get(SCREENS_VENDOR) + vendorOrdersList6.size());
        }

        //Count of collector per category
        hashMap.put(TEMP_COLLECTOR,0);
        hashMap.put(LAPMS_COLLECTOR,0);
        hashMap.put(LARGE_EQIP_COLLECTOR,0);
        hashMap.put(SMALL_EQUIP_COLLECTOR,0);
        hashMap.put(SMALL_IT_COLLECTOR,0);
        hashMap.put(SCREENS_COLLECTOR,0);

        for (Collector collector : collectorList) {
            List<CategoriesAccepted> list1 = categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, collector.getId());
            hashMap.put(TEMP_COLLECTOR, hashMap.get(TEMP_COLLECTOR) + list1.size());

            List<CategoriesAccepted> list2 = categoriesAcceptedRepo.findAllByCategoryAccepted(LAPMS, collector.getId());
            hashMap.put(LAPMS_COLLECTOR, hashMap.get(LAPMS_COLLECTOR) + list2.size());

            List<CategoriesAccepted> list3 = categoriesAcceptedRepo.findAllByCategoryAccepted(LARGE_EQIP, collector.getId());
            hashMap.put(LARGE_EQIP_COLLECTOR, hashMap.get(LARGE_EQIP_COLLECTOR) + list3.size());

            List<CategoriesAccepted> list4 = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_EQUIP, collector.getId());
            hashMap.put(SMALL_EQUIP_COLLECTOR, hashMap.get(SMALL_EQUIP_COLLECTOR) + list4.size());

            List<CategoriesAccepted> list5 = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_IT, collector.getId());
            hashMap.put(SMALL_IT_COLLECTOR, hashMap.get(SMALL_IT_COLLECTOR) + list5.size());

            List<CategoriesAccepted> list6 = categoriesAcceptedRepo.findAllByCategoryAccepted(SCREENS, collector.getId());
            hashMap.put(SCREENS_COLLECTOR, hashMap.get(SCREENS_COLLECTOR) + list6.size());
        }
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(hashMap);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);

    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAnalyticsV4(HttpServletRequest request) {
        String email = request.getHeader(EMAIL);
        Vendor vendor = vendorRepo.findByEmail(email);

        List<Collector> collectorList = collectorRepo.findByCity(vendor.getCity());

        HashMap<String,Integer>hashMap=new HashMap<>();
        hashMap.put(TEMP_CITY,0);
        hashMap.put(LAPMS_CITY,0);
        hashMap.put(LARGE_EQIP_CITY,0);
        hashMap.put(SMALL_EQUIP_CITY,0);
        hashMap.put(SMALL_IT_CITY,0);
        hashMap.put(SCREENS_CITY,0);


        for (Collector collector : collectorList) {
            List<CategoriesAccepted> list1 = categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, collector.getId());
            hashMap.put(TEMP_CITY, hashMap.get(TEMP_CITY) + list1.size());

            List<CategoriesAccepted> list2 = categoriesAcceptedRepo.findAllByCategoryAccepted(LAPMS, collector.getId());
            hashMap.put(LAPMS_CITY, hashMap.get(LAPMS_CITY) + list2.size());

            List<CategoriesAccepted> list3 = categoriesAcceptedRepo.findAllByCategoryAccepted(LARGE_EQIP, collector.getId());
            hashMap.put(LARGE_EQIP_CITY, hashMap.get(LARGE_EQIP_CITY) + list3.size());

            List<CategoriesAccepted> list4 = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_EQUIP, collector.getId());
            hashMap.put(SMALL_EQUIP_CITY, hashMap.get(SMALL_EQUIP_CITY) + list4.size());

            List<CategoriesAccepted> list5 = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_IT, collector.getId());
            hashMap.put(SMALL_IT_CITY,  hashMap.get(SMALL_IT_CITY) + list5.size());

            List<CategoriesAccepted> list6 = categoriesAcceptedRepo.findAllByCategoryAccepted(SCREENS, collector.getId());
            hashMap.put(SCREENS_CITY, hashMap.get(SCREENS_CITY) + list6.size());
        }
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(hashMap);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }



}
