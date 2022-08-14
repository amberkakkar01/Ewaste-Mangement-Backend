package com.groupfive.ewastemanagement.service.customerservice.order;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.collectorentity.AllPendingRequest;
import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.customerentity.Customer;
import com.groupfive.ewastemanagement.entity.customerentity.Orders;
import com.groupfive.ewastemanagement.entity.notification.Notification;
import com.groupfive.ewastemanagement.model.RequestModel;
import com.groupfive.ewastemanagement.repository.collectorrepository.AllPendingRequestRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.CustomerRepo;
import com.groupfive.ewastemanagement.repository.customerrepository.OrdersRepo;
import com.groupfive.ewastemanagement.repository.notification.NotificationRepo;
import com.groupfive.ewastemanagement.service.customerservice.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.groupfive.ewastemanagement.helper.Constants.*;

@Service
public class OrderServiceImplementation implements OrderService {

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private CollectorRepo collectorRepo;

    @Autowired
    private CustomerService checkService;

    @Autowired
    private AllPendingRequestRepo allPendingRequestRepo;

    @Autowired
    private CategoriesAcceptedRepo categoriesAcceptedRepo;

    @Autowired
    private NotificationRepo notificationRepo;
    @Override
    public ResponseEntity<EnvelopeMessage> createPickUpRequest(RequestModel requestModel, HttpServletRequest request) {
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        Orders orders = new Orders();

        String email=request.getHeader(EMAIL);

        Customer customer=customerRepo.findByEmail(email);
        orders.setCategory(requestModel.getCategory());
        orders.setQuantity(requestModel.getQuantity());
        orders.setCustomer(customer);
        orders.setRequestType(PICK_UP);

        orders.setPinCode(customer.getPinCode());
        orders.setCustomerUid(customer.getUid());

        orders.setStatus(PENDING);
        orders.setCity(customer.getCity());
        orders.setState(customer.getState());
        orders.setAddress(customer.getAddress1());

        orders.setOrderUid(orders.getOrderUid());
        orders.setItemName(requestModel.getItemName());
        orders.setScheduledDate(requestModel.getScheduledDate());
        orders.setScheduledTime(requestModel.getScheduledTime());

        List<String> uidList = checkService.getCollectorUidBasedOnCity(customer.getCity(), orders.getCategory());

        if (uidList.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_COLLECTOR_FOUND);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
        }

        for(int i=0;i<uidList.size();i++)
        {
            AllPendingRequest pendingRequest=new AllPendingRequest();

            pendingRequest.setOrderId(orders.getOrderUid());
            pendingRequest.setCollectorUid(uidList.get(i));

            pendingRequest.setStatus(PENDING);

            pendingRequest.setCategory(orders.getCategory());
            pendingRequest.setQuantity(orders.getQuantity());
            pendingRequest.setRequestType(orders.getRequestType());
            pendingRequest.setAddress(orders.getAddress()+", "+orders.getCity()+", "+orders.getState());
            pendingRequest.setScheduleDate(orders.getScheduledDate());
            pendingRequest.setScheduledTime(orders.getScheduledTime());
            pendingRequest.setItemName(orders.getItemName());

            allPendingRequestRepo.save(pendingRequest);

            //NOTI
            Notification notification = new Notification();
            notification.setCollectorUid(uidList.get(i));
            notification.setRole("Collector");
            notification.setMessage("One Pick-Up Request is Pending");
            Collector collector=collectorRepo.findByUid(uidList.get(i));
            notification.setCollector(collector);
            notification.setStatus(false);
            notificationRepo.save(notification);

        }
        ordersRepo.save(orders);

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(orders);

        return new ResponseEntity<>(envelopeMessage, HttpStatus.OK);
    }

    @Override
    public int showDropOffLocation(String category,HttpServletRequest request)
    {
        String email=request.getHeader(EMAIL);

        Customer customer=customerRepo.findByEmail(email);

        String city=customer.getCity();

        List<Collector>list=collectorRepo.findByCity(city);
        List<String> categoryList=categoriesAcceptedRepo.findByCategoryAccepted(category);
        List<Collector>catListCollector=new ArrayList<>();
        for (int i=0;i<categoryList.size();i++)
        {
            catListCollector.add(collectorRepo.findByUid(categoryList.get(i)));
        }

        list.retainAll(catListCollector);

        return list.size();
    }

    @Override
    public ResponseEntity<EnvelopeMessage> viewListOfCollectorsForDropOff(String category, HttpServletRequest request) {

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        String email=request.getHeader(EMAIL);

        Customer customer=customerRepo.findByEmail(email);

        String city=customer.getCity();

        List<Collector>list=collectorRepo.findByCity(city);
        List<String> categoryList=categoriesAcceptedRepo.findByCategoryAccepted(category);

        List<Collector>catListCollector=new ArrayList<>();
        for (int i=0;i<categoryList.size();i++)
        {
            catListCollector.add(collectorRepo.findByUid(categoryList.get(i)));
        }

        list.retainAll(catListCollector);


        if (list.isEmpty())
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData("No Collector in your area accepting entered category");
            return new ResponseEntity<>(envelopeMessage,HttpStatus.BAD_REQUEST);
        }

        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(list);

        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }

    @Override
    public int countCollectorPickUp(String category, HttpServletRequest request) {
        String email=request.getHeader(EMAIL);

        Customer customer=customerRepo.findByEmail(email);

        String city=customer.getCity();

        List<Collector> list=collectorRepo.findByCity(city);
        List<String> categoryList=categoriesAcceptedRepo.findByCategoryAccepted(category);
        List<String> cityList=new ArrayList<>();
        for(int i=0;i<list.size();i++)
        {
            cityList.add(list.get(i).getUid());
        }
        categoryList.retainAll(cityList);
        return categoryList.size();
    }

    @Override
    public ResponseEntity<EnvelopeMessage> createDropOffRequest(RequestModel requestModel, HttpServletRequest request) {
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        Orders orders = new Orders();
        String email=request.getHeader(EMAIL);

        Customer customer=customerRepo.findByEmail(email);
        orders.setCategory(requestModel.getCategory());
        orders.setQuantity(requestModel.getQuantity());
        orders.setRequestType(DROP_OFF);

        orders.setCustomer(customer);
        Collector collector=collectorRepo.findByUid(requestModel.getCollectorUid());
        orders.setCollector(collector);

        orders.setPinCode(customer.getPinCode());
        orders.setCustomerUid(customer.getUid());
        orders.setStatus(PENDING);

        orders.setOrderUid(orders.getOrderUid());
        orders.setItemName(requestModel.getItemName());
        orders.setScheduledDate(requestModel.getScheduledDate());
        orders.setScheduledTime(requestModel.getScheduledTime());

        orders.setAddress(customer.getAddress1());
        orders.setState(customer.getState());
        orders.setCity(customer.getCity());

        //collector uid in string from front end
        orders.setCollectorUid(requestModel.getCollectorUid());

        ordersRepo.save(orders);
        envelopeMessage.setStatus(SUCCESS);
        envelopeMessage.setData(orders);
        return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
    }
}
