package com.groupfive.ewastemanagement.service.customerservice;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.collectorentity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.collectorentity.EWasteDrive;
import com.groupfive.ewastemanagement.entity.customerentity.Customer;
import com.groupfive.ewastemanagement.entity.customerentity.Orders;
import com.groupfive.ewastemanagement.entity.notification.Notification;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.repository.collectorrepository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.EWasteDriveRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.CustomerRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.OrdersRepo;
import com.groupfive.ewastemanagement.repository.notification.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

import static com.groupfive.ewastemanagement.helper.Constants.*;

@Service
public class CustomerServiceImplementation implements CustomerService {

    @Autowired
    public CategoriesAcceptedRepo categoriesAcceptedRepo;

    @Autowired
    public CollectorRepo collectorRepo;

    @Autowired
    public CustomerRepo customerRepo;

    @Autowired
    public OrdersRepo ordersRepo;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public EWasteDriveRepo eWasteDriveRepo;

    @Autowired
    public NotificationRepo notificationRepo;

   
    @Override
    public List<String> getCollectorUidBasedOnCity(String city, String category) {
        List<Collector> listCity = collectorRepo.findByCity(city);
        List<String> listCategory = categoriesAcceptedRepo.findByCategoryAccepted(category);

        List<String>list=new ArrayList<>();

        for (int i=0;i<listCity.size();i++)
        {
            list.add(listCity.get(i).getUid());
        }

        list.retainAll(listCategory);

        return list;

    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAllOrders(HttpServletRequest request) {
        String email=request.getHeader(Constants.EMAIL);

        Customer customer=customerRepo.findByEmail(email);
        String uid=customer.getUid();

        List<Orders>list=ordersRepo.findAllByCustomerUid(uid);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (list.isEmpty())
        {
            envelopeMessage.setStatus(Constants.FAIL);
            envelopeMessage.setData(NO_ORDERS);

            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(list);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewProfile(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);

        Customer customer=customerRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(customer);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> editProfile(UserModel userModel, HttpServletRequest request) {

        String email=request.getHeader(EMAIL);

        Customer customer=customerRepo.findByEmail(email);

        customer.setEmail(userModel.getEmail());
        customer.setFirstName(userModel.getFirstName());
        customer.setLastName(userModel.getLastName());

        customer.setPassword(passwordEncoder.encode(userModel.getPassword()));
        customer.setMobileNo(userModel.getMobileNo());
        customer.setAddress1(userModel.getAddress1());
        customer.setCity(userModel.getCity());
        customer.setState(userModel.getState());
        customer.setPinCode(userModel.getPinCode());

        customerRepo.save(customer);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(customer);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewDrives(HttpServletRequest request) {

        String email = request.getHeader(EMAIL);
        Customer customer = customerRepo.findByEmail(email);

        List<EWasteDrive> listDrive = eWasteDriveRepo.findByStatus(Constants.UPCOMING);
        List<EWasteDrive> listDriveCity = eWasteDriveRepo.findAllByCity(customer.getCity());

        listDrive.retainAll(listDriveCity);
        String s= String.valueOf(LocalDate.now());

        String currentYear=s.substring(0,4);
        String currentMonth=s.substring(5,7);
        String currentDate=s.substring(8);

        int dateCurrent=Integer.parseInt(currentDate);
        int monthCurrent=Integer.parseInt(currentMonth);
        int yearCurrent=Integer.parseInt(currentYear);


        for (int i=0;i<listDrive.size();i++)
        {
            EWasteDrive eWaste=listDrive.get(i);

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
                    eWaste.setStatus(Constants.COMPLETED);
                    eWasteDriveRepo.save(eWaste);
                }

            }

        }
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (listDrive.isEmpty())
        {
            envelopeMessage.setStatus(Constants.FAIL);
            envelopeMessage.setData("No Drives In your Area");

            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(listDrive);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);

    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewNotification(HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);
        Customer customer= customerRepo.findByEmail(email);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        List<Notification> notifications = notificationRepo.findByRoleAndStatusAndCustomerUid("Customer",false,customer.getUid());

        if (notifications.isEmpty())
        {
            envelopeMessage.setStatus(Constants.FAIL);
            envelopeMessage.setData(NO_NEW_NOTIFICATION);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(notifications);
        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewCollectorProfile(String uid) {
        Collector collector=collectorRepo.findByUid(uid);

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        if (collector==null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(ORDER_NOT_ACCEPTED_BY_COLLECTOR);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(collector);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> readNotification(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);
        Customer customer= customerRepo.findByEmail(email);

        List<Notification> notificationList = notificationRepo.findByRoleAndStatusAndCustomerUid("Customer",false,customer.getUid());

        for (int i=0;i<notificationList.size();i++)
        {
            Notification customerNotification=notificationList.get(i);
            customerNotification.setStatus(true);
            notificationRepo.save(customerNotification);
        }
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (notificationList.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_UNREAD_NOTIFICATION);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(notificationList);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> getAnalytics(HttpServletRequest request) {
        String email=request.getHeader(EMAIL);
        Customer customer= customerRepo.findByEmail(email);

        HashMap<String,Integer>hashMap=new HashMap<>();

        List<Orders>orderInCity=ordersRepo.findAllByCity(customer.getCity());
        List<Orders>orderCustomer=ordersRepo.findAllByCustomerUid(customer.getUid());

        hashMap.put(ORDER_IN_CITY,orderInCity.size());
        hashMap.put(ORDER_CUSTOMER,orderCustomer.size());

        EnvelopeMessage envelopeMessage = new EnvelopeMessage();
        envelopeMessage.setData(hashMap);
        envelopeMessage.setStatus(SUCCESS);
        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }
    @Override
    public ResponseEntity<EnvelopeMessage> getAnalyticsV2(HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);
        Customer customer= customerRepo.findByEmail(email);

        List<EWasteDrive> eWasteDriveListCity = eWasteDriveRepo.findAllByCity(customer.getCity());
        List<EWasteDrive> eWasteDriveListAll = eWasteDriveRepo.findAll();

        HashMap<String,Integer>hashMap=new HashMap<>();
        hashMap.put(E_WASTE_DRIVE_LIST_CITY,eWasteDriveListCity.size());
        hashMap.put(E_WASTE_DRIVE_LIST_ALL,eWasteDriveListAll.size());

        EnvelopeMessage envelopeMessage = new EnvelopeMessage();
        envelopeMessage.setData(hashMap);
        envelopeMessage.setStatus(SUCCESS);
        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);

    }


    @Override
    public ResponseEntity<EnvelopeMessage> getAnalyticsV3(HttpServletRequest request) {
        String email = request.getHeader(Constants.EMAIL);
        Customer customer = customerRepo.findByEmail(email);

        List<Collector> collectorList = collectorRepo.findByCity(customer.getCity());
        List<Collector> collectorTotalList = collectorRepo.findAll();

        HashMap<String,Integer>hashMap=new HashMap<>();
        hashMap.put(TEMP_CITY,0);
        hashMap.put(LAPMS_CITY,0);
        hashMap.put(LARGE_EQIP_CITY,0);
        hashMap.put(SMALL_EQUIP_CITY,0);
        hashMap.put(SMALL_IT_CITY,0);
        hashMap.put(SCREENS_CITY,0);

        for (int i=0;i<collectorList.size();i++)
        {
            List<CategoriesAccepted> listTemp = categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, collectorList.get(i).getId());
            hashMap.put(TEMP_CITY, hashMap.get(TEMP_CITY) + listTemp.size());

            List<CategoriesAccepted> listLapms = categoriesAcceptedRepo.findAllByCategoryAccepted(LAPMS, collectorList.get(i).getId());
            hashMap.put(LAPMS_CITY, hashMap.get(LAPMS_CITY) + listLapms.size());

            List<CategoriesAccepted> listLargeEquip = categoriesAcceptedRepo.findAllByCategoryAccepted(LARGE_EQIP, collectorList.get(i).getId());
            hashMap.put(LARGE_EQIP_CITY, hashMap.get(LARGE_EQIP_CITY) + listLargeEquip.size());

            List<CategoriesAccepted> listSmallEqip = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_EQUIP, collectorList.get(i).getId());
            hashMap.put(SMALL_EQUIP_CITY, hashMap.get(SMALL_EQUIP_CITY) + listSmallEqip.size());

            List<CategoriesAccepted> listSmallIT = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_IT, collectorList.get(i).getId());
            hashMap.put(SMALL_IT_CITY, hashMap.get(SMALL_IT_CITY) + listSmallIT.size());

            List<CategoriesAccepted> listScreens = categoriesAcceptedRepo.findAllByCategoryAccepted(SCREENS, collectorList.get(i).getId());
            hashMap.put(SCREENS_CITY,  hashMap.get(SCREENS_CITY) + listScreens.size());
        }

        //new
        hashMap.put(TEMP_TOTAL,0);
        hashMap.put(LAPMS_TOTAL,0);
        hashMap.put(LARGE_EQIP_TOTAL,0);
        hashMap.put(SMALL_EQUIP_TOTAL,0);
        hashMap.put(SMALL_IT_TOTAL,0);
        hashMap.put(SCREENS_TOTAL,0);

        for (int i=0;i<collectorTotalList.size();i++)
        {
            List<CategoriesAccepted> listTemp = categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, collectorTotalList.get(i).getId());
            hashMap.put(TEMP_TOTAL,  hashMap.get(TEMP_TOTAL) + listTemp.size());

            List<CategoriesAccepted> listLapms = categoriesAcceptedRepo.findAllByCategoryAccepted(LAPMS, collectorTotalList.get(i).getId());
            hashMap.put(LAPMS_TOTAL, hashMap.get(LAPMS_TOTAL) + listLapms.size());

            List<CategoriesAccepted> listLargeEqip = categoriesAcceptedRepo.findAllByCategoryAccepted(LARGE_EQIP, collectorTotalList.get(i).getId());
            hashMap.put(LARGE_EQIP_TOTAL, hashMap.get(LARGE_EQIP_TOTAL)+listLargeEqip.size());

            List<CategoriesAccepted> listSmallEquip = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_EQUIP, collectorTotalList.get(i).getId());
            hashMap.put(SMALL_EQUIP_TOTAL, hashMap.get(SMALL_EQUIP_TOTAL) + listSmallEquip.size());

            List<CategoriesAccepted> listSmallIT = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_IT, collectorTotalList.get(i).getId());
            hashMap.put(SMALL_IT_TOTAL, hashMap.get(SMALL_IT_TOTAL) + listSmallIT.size());

            List<CategoriesAccepted> listScreens = categoriesAcceptedRepo.findAllByCategoryAccepted(SCREENS, collectorTotalList.get(i).getId());
            hashMap.put(SCREENS_TOTAL, hashMap.get(SCREENS_TOTAL) + listScreens.size());
        }
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(hashMap);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }
}
