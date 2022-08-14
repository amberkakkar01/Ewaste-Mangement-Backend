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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static com.groupfive.ewastemanagement.helper.Constants.*;

@Service
public class CollectorServiceImplementation implements CollectorService{


    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CollectorRepo collectorRepo;

    @Autowired
    private AllPendingRequestRepo allPendingRequestRepo;

    @Autowired
    private EWasteDriveRepo eWasteDriveRepo;

    @Autowired
    private SellItemRepo sellItemRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private VendorOrdersRepo vendorOrdersRepo;

    @Autowired
    private VendorRepo vendorRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @Override
    public ResponseEntity<EnvelopeMessage> getPendingRequest(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);
        Collector collector= collectorRepo.findByEmail(email);//cookies

        List<AllPendingRequest> allPendingRequest=allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(),PENDING);
        String s= String.valueOf(LocalDate.now());
        String currentYear=s.substring(0,4);
        String currentMonth=s.substring(5,7);
        String currentDate=s.substring(8);

        int dateCurrent=Integer.parseInt(currentDate);
        int monthCurrent=Integer.parseInt(currentMonth);
        int yearCurrent=Integer.parseInt(currentYear);

        for (int i=0;i<allPendingRequest.size();i++)
        {
            AllPendingRequest pendingRequest=allPendingRequest.get(i);
            String date= pendingRequest.getScheduleDate();
            String dataMonth=date.substring(5,7);
            String dataDate=date.substring(8);
            String dataYear=date.substring(0,4);
            int monthDatabase=Integer.parseInt(dataMonth);
            int dateDatabase=Integer.parseInt(dataDate);
            int yearDatabase=Integer.parseInt(dataYear);

            if (monthDatabase==monthCurrent && yearDatabase==yearCurrent)
            {
                int value=dateDatabase-dateCurrent;

                if (value<0)
                {
                    pendingRequest.setStatus(EXPIRED);

                    Orders orders=ordersRepo.findByOrderUid(pendingRequest.getOrderId());
                    orders.setStatus(EXPIRED);
                    ordersRepo.save(orders);
                    allPendingRequestRepo.save(pendingRequest);
                }

            }

        }

        List<AllPendingRequest> deletePendingRequest=allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(),EXPIRED);
        for (int i=0;i<deletePendingRequest.size();i++)
        {
            allPendingRequestRepo.delete(deletePendingRequest.get(i));
        }

        allPendingRequest = allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(),PENDING);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (allPendingRequest.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_REQUEST_PENDING);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(allPendingRequest);

        return new ResponseEntity<>(envelopeMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> getRequestSummary(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);

        Collector collector= collectorRepo.findByEmail(email);//cookies
        String uid=collector.getUid();

        List<Orders> listOrders =  ordersRepo.findByCollectorUid(uid);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (listOrders.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_ORDERS);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(listOrders);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> acceptPendingRequest(String orderId, HttpServletRequest request) {
        String email=request.getHeader(EMAIL);

        Collector collector= collectorRepo.findByEmail(email);//cookies

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        String uid=collector.getUid();

        allPendingRequestRepo.deleteAllByOrderId(orderId);
        Orders order=ordersRepo.findByOrderUid(orderId);

        if (order==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_SUCH_ORDER_EXIST);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        order.setCollectorUid(uid);
        order.setStatus(SCHEDULED);
        order.setCollector(collector);
        Customer customer = customerRepo.findByUid(order.getCustomerUid());
        order.setCustomer(customer);

        Notification customerNotification = new Notification();
        customerNotification.setCustomerUid(order.getCustomerUid());
        customerNotification.setStatus(false);
        customerNotification.setRole("Customer");
        customerNotification.setCustomer(customer);
        customerNotification.setMessage("Hi "+ customer.getFirstName() + " " + "your request for " + order.getItemName() + " is accepted by the collector");

        ordersRepo.save(order);
        notificationRepo.save(customerNotification);

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(order);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> organizeDrive(EWasteDriveModel eWasteDriveModel,HttpServletRequest request) {
        String email=request.getHeader(EMAIL);

        Collector collector=collectorRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (eWasteDriveModel==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_DATA_PROVIDED);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        EWasteDrive eWasteDrive=new EWasteDrive();
        eWasteDrive.setDriveName(eWasteDriveModel.getDriveName());
        eWasteDrive.setCollector(collector);
        eWasteDrive.setOrganizerName(collector.getFirstName() + " " +collector.getLastName());
        eWasteDrive.setDescription(eWasteDriveModel.getDescription());
        eWasteDrive.setCity(collector.getCity());

        eWasteDrive.setCollectorEmail(email);

        eWasteDrive.seteWasteCategoryAccepted(eWasteDriveModel.geteWasteCategoryAccepted());
        eWasteDrive.setDate(eWasteDriveModel.getDate());
        eWasteDrive.setTime(eWasteDriveModel.getTime());
        eWasteDrive.setLocation(collector.getAddress1() +" "+collector .getCity() + " "+collector.getState());
        eWasteDrive.setStatus(eWasteDriveModel.getStatus());

        eWasteDriveRepo.save(eWasteDrive);

//        CustomerNotification customerNotification = new CustomerNotification();
//        List<Customer> customerList = customerRepo.findByCity(collector.getCity());
//        for (int i=0;i<customerList.size();i++)
//        {
//            customerNotification.setCustomerUid(customerList.get(i).getUid());
//            customerNotification.setCustomer(customerList.get(i));
//            customerNotification.setStatus(false);
//            customerNotification.setMessage("Hi! E-Waste Drive is Scheduled near "+ eWasteDrive.getLocation() + " do check it out");
//            customerNotificationRepo.save(customerNotification);
//        }

        Notification notification = new Notification();
        List<Customer> customerList = customerRepo.findByCity(collector.getCity());
        for (int i=0;i<customerList.size();i++)
        {
            notification.setCustomerUid(customerList.get(i).getUid());
            notification.setCustomer(customerList.get(i));
            notification.setRole("Customer");
            notification.setStatus(false);
            notification.setMessage("Hi! E-Waste Drive is Scheduled near "+ eWasteDrive.getLocation() + " do check it out");
            notificationRepo.save(notification);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(eWasteDrive);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> sellItem(SellItemModel sellItemModel, HttpServletRequest request) {
        String email=request.getHeader(EMAIL);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        if (sellItemModel==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(ENTER_ALL_DETAILS);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        Collector collector= collectorRepo.findByEmail(email);

        SellItems sellItems=new SellItems();
        sellItems.setCollectorUid(collector.getUid());
        sellItems.setItemName(sellItemModel.getItemName());
        sellItems.setCategory(sellItemModel.getCategory());
        sellItems.setQuantity(sellItemModel.getQuantity());
        sellItems.setPrice(sellItemModel.getPrice());
        sellItems.setCollector(collector);
        sellItems.setAvailableQuantity(sellItemModel.getQuantity());

        sellItems.setStatus(sellItemModel.getStatus());

        sellItemRepo.save(sellItems);


        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(sellItems);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }


    @Override
    public ResponseEntity<EnvelopeMessage> viewMyDrive(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        List<EWasteDrive>eWasteDrive=eWasteDriveRepo.getAllByCollectorEmail(email);

        if (eWasteDrive.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(ENTER_ALL_DETAILS);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        String s= String.valueOf(LocalDate.now());

        String currentYear=s.substring(0,4);
        String currentMonth=s.substring(5,7);
        String currentDate=s.substring(8);

        int dateCurrent=Integer.parseInt(currentDate);
        int monthCurrent=Integer.parseInt(currentMonth);
        int yearCurrent=Integer.parseInt(currentYear);


        for (int i=0;i<eWasteDrive.size();i++)
        {
            EWasteDrive eWaste=eWasteDrive.get(i);

            String date=eWaste.getDate();

            String dataMonth=date.substring(5,7);
            String dataDate=date.substring(8);
            String dataYear=date.substring(0,4);

            int monthDatabase=Integer.parseInt(dataMonth);
            int dateDatabase=Integer.parseInt(dataDate);
            int yearDatabase=Integer.parseInt(dataYear);

            if (monthDatabase==monthCurrent && yearDatabase==yearCurrent)
            {
                int value=dateDatabase-dateCurrent;

                if (value<0)
                {
                    eWaste.setStatus(COMPLETED);
                    eWasteDriveRepo.save(eWaste);
                }

            }

        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(eWasteDrive);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewProfile(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        Collector collector=collectorRepo.findByEmail(email);

        if (collector==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_SUCH_USER_FOUND);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(collector);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> editProfile(UserModel userModel, HttpServletRequest request) {

        String email=request.getHeader(EMAIL);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        Collector collector= collectorRepo.findByEmail(email);

        if (collector==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_SUCH_USER_FOUND);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        collector.setEmail(userModel.getEmail());
        collector.setFirstName(userModel.getFirstName());
        collector.setLastName(userModel.getLastName());
        collector.setPassword(passwordEncoder.encode(userModel.getPassword()));
        collector.setMobileNo(userModel.getMobileNo());

        collector.setAddress1(userModel.getAddress1());
        collector.setCity(userModel.getCity());
        collector.setState(userModel.getState());
        collector.setPinCode(userModel.getPinCode());

        collector.setGstNo(userModel.getGstNo());
        collector.setRegistrationNo(userModel.getRegistrationNo());
        collector.setCategoriesAcceptedSet(userModel.getCategoriesAcceptedSet());
        collector.setShopTime(userModel.getShopTime());

        collectorRepo.save(collector);

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(collector);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewNotification(HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);
        Collector collector= collectorRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        List<Notification>list=notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid());
        if (list.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_NEW_NOTIFICATION);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(list);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> sellItemAvailableSummary(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);
        Collector collector= collectorRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        List<SellItems>sellItems=sellItemRepo.findAllByCollectorUidAndStatus(collector.getUid(),AVAILABLE);

        if (sellItems.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_ITEMS_IN_SALE);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(sellItems);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> sellItemSoldSummary(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);
        Collector collector= collectorRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        List<VendorOrders>vendorOrders=vendorOrdersRepo.findAllByCollectorUid(collector.getUid());

        if (vendorOrders.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_ITEMS_IN_SALE);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(vendorOrders);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> editDriveSummary(Long id, String status, HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);
        Collector collector= collectorRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        EWasteDrive eWasteDrive=eWasteDriveRepo.findByCollectorEmailAndId(collector.getEmail(),id);

        if (eWasteDrive==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(DRIVE_NOT_FOUND);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        eWasteDrive.setStatus(status);

        eWasteDriveRepo.save(eWasteDrive);

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(eWasteDrive);
        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewCustomerProfile(String uid) {
        Customer customer=customerRepo.findByUid(uid);
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (customer==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(PROFILE_NOT_FOUND);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(customer);
        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAnalytics(HttpServletRequest request)
    {
        String email = request.getHeader(EMAIL);
        Collector collector = collectorRepo.findByEmail(email);

        List<EWasteDrive> eWasteDriveList = eWasteDriveRepo.getAllByCollectorEmail(collector.getEmail());
        List<EWasteDrive> eWasteDriveCityList = eWasteDriveRepo.findAllByCity(collector.getCity());
        HashMap<String,Integer> hm = new HashMap<>();

        hm.put(E_WASTE_DRIVE_COLLECTOR,eWasteDriveList.size());
        hm.put(E_WASTE_DRIVE_CITY,eWasteDriveCityList.size());

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(hm);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAnalyticsV2(HttpServletRequest request)
    {
        String email = request.getHeader(EMAIL);
        Collector collector = collectorRepo.findByEmail(email);

        List<Collector> collectorList = collectorRepo.findByCity(collector.getCity());

        HashMap<String,Integer>hashMap=new HashMap<>();

        hashMap.put(TEMP_COLLECTED,0);
        hashMap.put(LAPMS_COLLECTED,0);
        hashMap.put(LARGE_EQIP_COLLECTED,0);
        hashMap.put(SMALL_EQUIP_COLLECTED,0);
        hashMap.put(SMALL_IT_COLLECTED,0);
        hashMap.put(SCREENS_COLLECTED,0);


        for (int i=0;i<collectorList.size();i++)
        {

            List<Orders> listTemp = ordersRepo.findByCategoryAndCollectorUid(TEMP, collectorList.get(i).getUid());
            hashMap.put(TEMP_COLLECTED, hashMap.get(TEMP_COLLECTED) + listTemp.size());

            List<Orders> listLapms = ordersRepo.findByCategoryAndCollectorUid(LAPMS, collectorList.get(i).getUid());
            hashMap.put(LAPMS_COLLECTED, hashMap.get(LAPMS_COLLECTED) + listLapms.size());

            List<Orders> listLargeEqip = ordersRepo.findByCategoryAndCollectorUid(LARGE_EQIP, collectorList.get(i).getUid());
            hashMap.put(LARGE_EQIP_COLLECTED, hashMap.get(LARGE_EQIP_COLLECTED) + listLargeEqip.size());

            List<Orders> listSmallEquip = ordersRepo.findByCategoryAndCollectorUid(SMALL_EQUIP, collectorList.get(i).getUid());
            hashMap.put(SMALL_EQUIP_COLLECTED, hashMap.get(SMALL_EQUIP_COLLECTED) + listSmallEquip.size());

            List<Orders> listSmallIT = ordersRepo.findByCategoryAndCollectorUid(SMALL_IT, collectorList.get(i).getUid());
            hashMap.put(SMALL_IT_COLLECTED, hashMap.get(SMALL_IT_COLLECTED) + listSmallIT.size());

            List<Orders> listScreens = ordersRepo.findByCategoryAndCollectorUid(SCREENS, collectorList.get(i).getUid());
            hashMap.put(SCREENS_COLLECTED, hashMap.get(SCREENS_COLLECTED) + listScreens.size());

        }

        hashMap.put(TEMP_SELL,0);
        hashMap.put(LAPMS_SELL,0);
        hashMap.put(LARGE_EQIP_SELL,0);
        hashMap.put(SMALL_EQUIP_SELL,0);
        hashMap.put(SMALL_IT_SELL,0);
        hashMap.put(SCREENS_SELL,0);



        for (int i=0;i<collectorList.size();i++)
        {
            List<SellItems> listTemp = sellItemRepo.findByCategoryAndCollectorUid(TEMP, collectorList.get(i).getUid());
            hashMap.put(TEMP_SELL, hashMap.get(TEMP_SELL) + listTemp.size());

            List<SellItems> listLapms = sellItemRepo.findByCategoryAndCollectorUid(LAPMS, collectorList.get(i).getUid());
            hashMap.put(LAPMS_SELL,  hashMap.get(LAPMS_SELL) + listLapms.size());

            List<SellItems> listLargeEquip = sellItemRepo.findByCategoryAndCollectorUid(LARGE_EQIP, collectorList.get(i).getUid());
            hashMap.put(LARGE_EQIP_SELL, hashMap.get(LARGE_EQIP_SELL) + listLargeEquip.size());

            List<SellItems> listSmallEquip = sellItemRepo.findByCategoryAndCollectorUid(SMALL_EQUIP, collectorList.get(i).getUid());
            hashMap.put(SMALL_EQUIP_SELL, hashMap.get(SMALL_EQUIP_SELL) + listSmallEquip.size());

            List<SellItems> listSmallIT = sellItemRepo.findByCategoryAndCollectorUid(SMALL_IT, collectorList.get(i).getUid());
            hashMap.put(SMALL_IT_SELL, hashMap.get(SMALL_IT_SELL) + listSmallIT.size());

            List<SellItems> listScreens = sellItemRepo.findByCategoryAndCollectorUid(SCREENS, collectorList.get(i).getUid());
            hashMap.put(SCREENS_SELL, hashMap.get(SCREENS_SELL) + listScreens.size());

        }
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(hashMap);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> readNotification(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);
        Collector collector= collectorRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        List<Notification>list=notificationRepo.findByRoleAndStatusAndCollectorUid("Collector",false,collector.getUid());

        for (int i=0;i<list.size();i++)
        {
            Notification notification=list.get(i);
            notification.setStatus(true);
            notificationRepo.save(notification);
        }

        if (list.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_NEW_NOTIFICATION);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(list);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAnalyticsV4(HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);
        Collector collector=collectorRepo.findByEmail(email);

        List<Vendor> vendorList = vendorRepo.findByCity(collector.getCity());
        List<Vendor> vendorListAll = vendorRepo.findAll();

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put(VENDOR_CITY,vendorList.size());
        hashMap.put(VENDOR_ALL_CITY,vendorListAll.size());

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(hashMap);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAnalyticsV5(HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);
        Collector collector=collectorRepo.findByEmail(email);

        List<Customer> customerList = customerRepo.findByCity(collector.getCity());
        List<Customer> customerListAll = customerRepo.findAll();

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put(CUSTOMER_CITY,customerList.size());
        hashMap.put(CUSTOMER_ALL_CITY,customerListAll.size());

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(hashMap);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAnalyticsV6(HttpServletRequest request)
    {
        String email = request.getHeader(EMAIL);
        Collector collector = collectorRepo.findByEmail(email);

        List<VendorOrders> vendorOrdersListTemp = vendorOrdersRepo.findAllByCollectorUidAndCategory(collector.getUid(), TEMP);
        List<VendorOrders> vendorOrdersListSmallIT = vendorOrdersRepo.findAllByCollectorUidAndCategory(collector.getUid(), SMALL_IT);
        List<VendorOrders> vendorOrdersListLapms = vendorOrdersRepo.findAllByCollectorUidAndCategory(collector.getUid(), LAPMS);
        List<VendorOrders> vendorOrdersListScreens = vendorOrdersRepo.findAllByCollectorUidAndCategory(collector.getUid(), SCREENS);
        List<VendorOrders> vendorOrdersListSmallEquip = vendorOrdersRepo.findAllByCollectorUidAndCategory(collector.getUid(), SMALL_EQUIP);
        List<VendorOrders> vendorOrdersListLargeEqip = vendorOrdersRepo.findAllByCollectorUidAndCategory(collector.getUid(), LARGE_EQIP);

        int tempsum=0,smallitsum=0,lapmssum=0,screenssum=0,smallequip=0,largeeqip=0;
        for(int i=0;i<vendorOrdersListTemp.size();i++)
        {
            tempsum= tempsum + Integer.parseInt(vendorOrdersListTemp.get(i).getPrice());
        }
        for(int i=0;i<vendorOrdersListSmallIT.size();i++)
        {
            smallitsum= smallitsum + Integer.parseInt(vendorOrdersListSmallIT.get(i).getPrice());
        }
        for(int i=0;i<vendorOrdersListLapms.size();i++)
        {
            lapmssum= lapmssum + Integer.parseInt(vendorOrdersListLapms.get(i).getPrice());
        }
        for(int i=0;i<vendorOrdersListScreens.size();i++)
        {
            screenssum= screenssum + Integer.parseInt(vendorOrdersListScreens.get(i).getPrice());
        }
        for(int i=0;i<vendorOrdersListSmallEquip.size();i++)
        {
            smallequip= smallequip + Integer.parseInt(vendorOrdersListSmallEquip.get(i).getPrice());
        }
        for(int i=0;i<vendorOrdersListLargeEqip.size();i++)
        {
            largeeqip= largeeqip + Integer.parseInt(vendorOrdersListLargeEqip.get(i).getPrice());
        }
        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put(TEMP,tempsum);
        hashMap.put(SMALL_IT,smallitsum);
        hashMap.put(LAPMS,lapmssum);
        hashMap.put(SCREENS,screenssum);
        hashMap.put(SMALL_EQUIP,smallequip);
        hashMap.put(LARGE_EQIP,largeeqip);

        EnvelopeMessage envelopeMessage = new EnvelopeMessage();
        envelopeMessage.setData(hashMap);
        envelopeMessage.setStatus(SUCCESS);
        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> editSellItemAvailableSummary(HttpServletRequest request, SellItemModel sellItemModel)
    {
        String email=request.getHeader(EMAIL);
        Collector collector=collectorRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        SellItems sellItems = sellItemRepo.findById(sellItemModel.getId()).get();

        sellItems.setItemName(sellItemModel.getItemName());
        sellItems.setStatus(sellItemModel.getStatus());
        sellItems.setPrice(sellItemModel.getPrice());
        sellItems.setCategory(sellItemModel.getCategory());
        sellItems.setCollector(collector);
        sellItems.setCollectorUid(collector.getUid());

        int sellItemsQuantity=Integer.parseInt(sellItems.getQuantity());
        int sellItemsQuantityModel=Integer.parseInt(sellItemModel.getQuantity());
        int total=Integer.parseInt(sellItems.getAvailableQuantity()) + (sellItemsQuantityModel-sellItemsQuantity);
        if(total>0)
        {
            sellItems.setAvailableQuantity(String.valueOf(total));
            sellItems.setQuantity(sellItemModel.getQuantity());
        }
        else{
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData("Value can not be less than zero");
            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }
        sellItemRepo.save(sellItems);

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(sellItems);

        return  new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }
}