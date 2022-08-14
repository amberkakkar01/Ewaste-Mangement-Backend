package com.groupfive.ewastemanagement.service.implementation;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.*;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.dto.request.EWasteDriveDTO;
import com.groupfive.ewastemanagement.dto.request.SellItemDTO;
import com.groupfive.ewastemanagement.repository.AllPendingRequestRepo;
import com.groupfive.ewastemanagement.repository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.EWasteDriveRepo;
import com.groupfive.ewastemanagement.repository.SellItemRepo;
import com.groupfive.ewastemanagement.repository.OrdersRepo;
import com.groupfive.ewastemanagement.repository.NotificationRepo;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.repository.VendorOrdersRepo;
import com.groupfive.ewastemanagement.service.CollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.groupfive.ewastemanagement.helper.Constants.*;

import static com.groupfive.ewastemanagement.helper.Util.*;

@Service
public class CollectorServiceImplementation implements CollectorService {
    private final OrdersRepo ordersRepo;
    private final AllPendingRequestRepo allPendingRequestRepo;
    private final EWasteDriveRepo eWasteDriveRepo;
    private final SellItemRepo sellItemRepo;
    private final UserRepo userRepo;
    private final VendorOrdersRepo vendorOrdersRepo;
    private final NotificationRepo notificationRepo;
    private final CategoriesAcceptedRepo categoriesAcceptedRepo;
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorServiceImplementation.class);

    @Autowired
    public CollectorServiceImplementation(OrdersRepo ordersRepo, AllPendingRequestRepo allPendingRequestRepo, EWasteDriveRepo eWasteDriveRepo, SellItemRepo sellItemRepo, UserRepo userRepo, VendorOrdersRepo vendorOrdersRepo, NotificationRepo notificationRepo, CategoriesAcceptedRepo categoriesAcceptedRepo) {
        this.ordersRepo = ordersRepo;
        this.allPendingRequestRepo = allPendingRequestRepo;
        this.eWasteDriveRepo = eWasteDriveRepo;
        this.sellItemRepo = sellItemRepo;
        this.userRepo = userRepo;
        this.vendorOrdersRepo = vendorOrdersRepo;
        this.notificationRepo = notificationRepo;
        this.categoriesAcceptedRepo = categoriesAcceptedRepo;
    }

    /**
     * This function of the service marks the status of all the pending request to expired
     *
     * @param allPendingRequest AllPendingRequest Model
     */

    @Override
    public void expirePendingRequest(AllPendingRequest allPendingRequest) {
        allPendingRequest.setStatus(EXPIRED);
        Orders orders = ordersRepo.findByOrderUid(allPendingRequest.getOrderId());
        orders.setStatus(EXPIRED);
        LOGGER.info("Pending Request with order Id :: " + allPendingRequest.getOrderId() + " is marked as expired");
        ordersRepo.save(orders);
        allPendingRequestRepo.save(allPendingRequest);
    }

    /**
     * This function of the service provides all the pending request of collector
     *
     * @param request
     * @param pageNo   Integer Parameter that requests the page Number in Request Param
     * @param pageSize Integer Parameter that requests the page Size in Request Param
     * @return Response Entity with status code 200 and all pending requests in body
     * @throws BadRequestException
     * @throws NullPointerException
     */

    @Override
    public ResponseEntity<ResponseMessageWithPagination> getPendingRequest(int pageNo, int pageSize, HttpServletRequest request) throws BadRequestException, NullPointerException {
        LOGGER.info("Get Pending Request :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User collector;
        List<AllPendingRequest> allPendingRequest;
        ResponseMessageWithPagination responseMessage = new ResponseMessageWithPagination();

        try {
            collector = userRepo.findUserByEmail(email);//cookies
            LOGGER.info("Fetching all the Pending Request from the database");
            Page<AllPendingRequest> allPendingRequests = allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(), PENDING, PageRequest.of(pageNo - 1, pageSize));
            allPendingRequest = allPendingRequests.getContent();
            LOGGER.info("Retrieved all Pending Request from the database");

            for (int i = 0; i < allPendingRequest.size(); i++) {
                AllPendingRequest pendingRequest = allPendingRequest.get(i);
                String date = pendingRequest.getScheduleDate();
                LOGGER.info("Checking the expiry status of all Pending Request fetched from the database");

                if (checkExpiryOfRequest(date)) {
                    expirePendingRequest(pendingRequest);
                }

            }
            allPendingRequests = allPendingRequestRepo.findByCollectorUidAndStatus(collector.getUid(), PENDING, PageRequest.of(pageNo - 1, pageSize));
            allPendingRequest = allPendingRequests.getContent();


            if (allPendingRequest.isEmpty()) {
                LOGGER.error("No Pending Request available");
                responseMessage.setStatus(FAIL);
                responseMessage.setPageNo(pageNo);
                responseMessage.setPageSize(pageSize);
                responseMessage.setTotalRecords(allPendingRequest.size());
                responseMessage.setData(NO_REQUEST_PENDING);
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }

        } catch (NullPointerException nullPointerException) {
            LOGGER.error("The User doesn't exist in the database");
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        LOGGER.info("Pending Request " + FETCHED_SUCCESSFULLY);
        responseMessage.setStatus(SUCCESS);
        responseMessage.setPageNo(pageNo);
        responseMessage.setPageSize(pageSize);
        responseMessage.setTotalRecords(allPendingRequest.size());
        responseMessage.setData(allPendingRequest);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of the service provides all request summary of collector
     *
     * @param request
     * @param pageNo   Integer Parameter that requests the page Number in Request Param
     * @param pageSize Integer Parameter that requests the page Size in Request Param
     * @return Response Entity with status code 200 and get request summary in body
     * @throws BadRequestException
     * @throws InvalidUserException
     */

    @Override
    public ResponseEntity<ResponseMessageWithPagination> getRequestSummary(int pageNo, int pageSize, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Get Request Summary :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User collector;
        List<Orders> listOrders;
        ResponseMessageWithPagination responseMessage = new ResponseMessageWithPagination();
        try {
            collector = userRepo.findUserByEmail(email);
            Page<Orders> orders = ordersRepo.findByCollectorUidAndStatus(collector.getUid(), "Scheduled", PageRequest.of(pageNo - 1, pageSize));
            listOrders = orders.getContent();

            LOGGER.info("Fetching all the orders of collector from the database");

            if (listOrders.isEmpty()) {
                LOGGER.error(NO_ORDERS);

                responseMessage.setStatus(FAIL);
                responseMessage.setData(NO_ORDERS);
                responseMessage.setPageNo(pageNo);
                responseMessage.setPageSize(pageSize);
                responseMessage.setTotalRecords(orders.getTotalPages());

                return new ResponseEntity<>(responseMessage, HttpStatus.NO_CONTENT);
            }
            LOGGER.info("Order details " + FETCHED_SUCCESSFULLY);

            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(listOrders);
            responseMessage.setPageNo(pageNo);
            responseMessage.setPageSize(pageSize);
            responseMessage.setTotalRecords(orders.getTotalPages());
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of the service accepts pending request of collector
     *
     * @param orderId
     * @param request
     * @return Response Entity with status code 200 and details of accepted requests in body
     * @throws BadRequestException
     * @throws InvalidUserException
     */

    @Override
    public ResponseEntity<ResponseMessage> acceptPendingRequest(String orderId, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Accept Pending Request :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User collector;
        List<AllPendingRequest> allPendingRequestList;
        ResponseMessage responseMessage = new ResponseMessage();
        Orders order;

        if (userRepo.findUserByEmail(email) == null) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        collector = userRepo.findUserByEmail(email);

        LOGGER.info("Fetching details from the Pending Request with order Id :: '{}'", orderId);

        allPendingRequestList = allPendingRequestRepo.findByOrderId(orderId);

        for (int i = 0; i < allPendingRequestList.size(); i++) {
            allPendingRequestList.get(i).setStatus("Accepted");
        }
        order = ordersRepo.findByOrderUid(orderId);

        if (order == null) {
            LOGGER.error(NO_SUCH_ORDER_EXIST);
            responseMessage.setStatus(FAIL);
            responseMessage.setData(NO_SUCH_ORDER_EXIST);

            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        order.setCollectorUid(collector.getUid());
        order.setStatus(SCHEDULED);

        Notification customerNotification = new Notification();
        customerNotification.setCustomerUid(order.getCustomerUid());
        customerNotification.setStatus(false);
        customerNotification.setRole("Customer");
        User customer = userRepo.findUserByUid(order.getCustomerUid());
        customerNotification.setMessage("Hi " + customer.getEmail() + " " + "your request for " + order.getItemName() + " is accepted by the collector");

        LOGGER.info(NOTIFICATION_CREATED_WITH_ALL_THE_DETAILS_SUCCESSFULLY);

        ordersRepo.save(order);

        LOGGER.info("Order details saved successfully");

        notificationRepo.save(customerNotification);

        LOGGER.info(NOTIFICATION_SAVED_TO_DATABASE_WITH_ALL_DETAILS_SUCCESSFULLY);


        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(order);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }

    /**
     * This function of service is for organizing E-Waste drives
     *
     * @param eWasteDriveDTO which contains fields (driveName, categoryAccepted, date, time, status)
     * @param request
     * @return Response Entity with status code 201 and with all details of organized ewaste drive in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> organizeDrive(EWasteDriveDTO eWasteDriveDTO, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Organize E-Waste Drive :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        try {
            User collector;
            EWasteDrive eWasteDrive = new EWasteDrive();
            ResponseMessage responseMessage = new ResponseMessage();

            collector = userRepo.findUserByEmail(email);

            if (eWasteDriveDTO == null) {
                LOGGER.error(NO_DATA_PROVIDED);
                responseMessage.setStatus(FAIL);
                responseMessage.setData(NO_DATA_PROVIDED);

                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }

            eWasteDrive.setDriveName(eWasteDriveDTO.getDriveName());
            eWasteDrive.setOrganizerName(collector.getFirstName() + " " + collector.getLastName());
            eWasteDrive.setDescription(eWasteDriveDTO.getDescription());
            eWasteDrive.setCity(collector.getCity());
            eWasteDrive.setCollectorEmail(email);
            eWasteDrive.setCollector(collector);
            Set<CategoriesAccepted> finalSet = new HashSet<>();

            Set<CategoriesAccepted> categoriesAcceptedSet = eWasteDriveDTO.getCategoryAcceptedSet();


            for (CategoriesAccepted categoriesAccepted : categoriesAcceptedSet) {
                CategoriesAccepted category = categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted());
                finalSet.add(category);
            }

            eWasteDrive.setEWasteCategoryAccepted(finalSet);
            eWasteDrive.setDate(eWasteDriveDTO.getDate());
            eWasteDrive.setTime(eWasteDriveDTO.getTime());
            eWasteDrive.setLocation(collector.getAddress1() + " " + collector.getCity() + " " + collector.getState());
            eWasteDrive.setStatus(eWasteDriveDTO.getStatus());

            eWasteDrive.setCollector(collector);
            eWasteDriveRepo.save(eWasteDrive);
            LOGGER.info("Drive Scheduled Successfully");

            Notification notification = new Notification();
            List<User> customerList = userRepo.findAllUsersByRoleAndCity(CUSTOMER, collector.getCity());
            for (int i = 0; i < customerList.size(); i++) {
                notification.setCustomerUid(customerList.get(i).getUid());
                notification.setRole("Customer");
                notification.setStatus(false);
                LOGGER.info(NOTIFICATION_CREATED_WITH_ALL_THE_DETAILS_SUCCESSFULLY + " for " + collector.getEmail());
                notification.setMessage("Hi! E-Waste Drive is Scheduled near " + eWasteDrive.getLocation() + " do check it out");
                notificationRepo.save(notification);
                LOGGER.info(NOTIFICATION_CREATED_WITH_ALL_THE_DETAILS_SUCCESSFULLY + " for " + collector.getEmail());
            }

            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(eWasteDrive);

            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

    }

    /**
     * This function of service is for selling E-Waste Items
     *
     * @param sellItemDTO which contains fields (itemName, category, quantity, price, status)
     * @param request
     * @return Response Entity with status code 201 and details of item put on sale in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> sellItem(SellItemDTO sellItemDTO, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Sell Item :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User collector;
        SellItems sellItems = new SellItems();
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            collector = userRepo.findUserByEmail(email);//cookies
            if (sellItemDTO == null) {
                responseMessage.setStatus(FAIL);
                responseMessage.setData(ENTER_ALL_DETAILS);
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }

            sellItems.setCollectorUid(collector.getUid());
            sellItems.setItemName(sellItemDTO.getItemName());

            sellItems.setCategory(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(sellItemDTO.getCategory().getCategoryAccepted()));

            sellItems.setQuantity(sellItemDTO.getQuantity());
            sellItems.setPrice(sellItemDTO.getPrice());
            sellItems.setUser(collector);
            sellItems.setAvailableQuantity(sellItemDTO.getQuantity());

            sellItems.setStatus(sellItemDTO.getStatus());


            sellItemRepo.save(sellItems);
            LOGGER.info("Items on sale saved successfully");

        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(sellItems);

        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    /**
     * This function of service is for viewing all the scheduled E-Waste Drives by the coollector
     *
     * @param request
     * @param pageNo   Integer Parameter that requests the page Number in Request Param
     * @param pageSize Integer Parameter that requests the page Size in Request Param
     * @return Response Entity with status code 200 and list of all E-Waste drive in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessageWithPagination> viewMyDrive(int pageNo, int pageSize, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("View my drive :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        ResponseMessageWithPagination responseMessage = new ResponseMessageWithPagination();
        User collector;
        List<EWasteDrive> eWasteDrive;
        try {
            collector = userRepo.findUserByEmail(email);
            Page<EWasteDrive> eWasteDrives = eWasteDriveRepo.getAllByCollectorEmail(collector.getEmail(), PageRequest.of(pageNo - 1, pageSize));
            eWasteDrive = eWasteDrives.getContent();
            LOGGER.info("Fetching all the scheduled E-Waste drives from the database");
            if (eWasteDrive.isEmpty()) {
                LOGGER.error(NO_DRIVES_AVAILABLE);
                responseMessage.setStatus(FAIL);
                responseMessage.setPageNo(pageNo);
                responseMessage.setPageSize(pageSize);
                responseMessage.setTotalRecords(eWasteDrive.size());
                responseMessage.setData(NO_DRIVES_AVAILABLE);
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }

            for (int i = 0; i < eWasteDrive.size(); i++) {
                EWasteDrive eWaste = eWasteDrive.get(i);

                String date = eWaste.getDate();
                if (checkExpiryOfRequest(date)) {
                    LOGGER.info("Checking the expiry of E-Waste drive scheduled by :: " + eWaste.getOrganizerName());
                    eWaste.setStatus(COMPLETED);
                    eWasteDriveRepo.save(eWaste);
                }
            }
        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        LOGGER.info("Drives :: " + FETCHED_SUCCESSFULLY);

        responseMessage.setStatus(SUCCESS);
        responseMessage.setPageNo(pageNo);
        responseMessage.setPageSize(pageSize);
        responseMessage.setTotalRecords(eWasteDrive.size());
        responseMessage.setData(eWasteDrive);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for viewing notification
     *
     * @param request
     * @return Response Entity with status code 200 and list of unread notification in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> viewNotification(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Get Notification :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        List<Notification> list;
        try {
            user = userRepo.findUserByEmail(email);
            list = notificationRepo.findByRoleAndStatusAndCollectorUid("Collector", false, user.getUid());

        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        ResponseMessage responseMessage = new ResponseMessage();

        if (list.isEmpty()) {
            LOGGER.error("No Notification found");
            responseMessage.setStatus(FAIL);
            responseMessage.setData(NO_NEW_NOTIFICATION);
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("Notifications :: " + FETCHED_SUCCESSFULLY);

        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(list);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for viewing item available on sale
     *
     * @param request
     * @return Response Entity with status code 200 and list of items available on sale in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> sellItemAvailableSummary(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Get Available Sell Item Summary :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        List<SellItems> sellItemsList;
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            user = userRepo.findUserByEmail(email);
            sellItemsList = sellItemRepo.findAllByCollectorUidAndStatus(user.getUid(), AVAILABLE);
            if (sellItemsList.isEmpty()) {
                LOGGER.error(NO_ITEMS_IN_SALE);
                responseMessage.setStatus(FAIL);
                responseMessage.setData(NO_ITEMS_IN_SALE);
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        LOGGER.info("Available Sell Item Summary :: " + FETCHED_SUCCESSFULLY);

        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(sellItemsList);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for displaying items purchased by vendor
     *
     * @param request
     * @return Response Entity with status code 200 and displays the list of items purchased by vendor in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> sellItemSoldSummary(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Get Sold Sell Item Summary :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);


        User user;
        List<VendorOrders> vendorOrders;
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            user = userRepo.findUserByEmail(email);
            vendorOrders = vendorOrdersRepo.findAllByCollectorUid(user.getUid());

            if (vendorOrders.isEmpty()) {
                LOGGER.error(NO_ITEMS_IN_SALE);
                responseMessage.setStatus(FAIL);
                responseMessage.setData(NO_ITEMS_IN_SALE);
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }

        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        LOGGER.info("Sold Sell Item Summary :: " + FETCHED_SUCCESSFULLY);
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(vendorOrders);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for modifying the organized E-Waste Drive
     *
     * @param id
     * @param status
     * @param request
     * @return Response Entity with status code 200 and displays modified E-Waste drive in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> editDriveSummary(Long id, String status, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Edit Drive Summary :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        EWasteDrive eWasteDrive;
        try {
            user = userRepo.findUserByEmail(email);
            eWasteDrive = eWasteDriveRepo.findByCollectorEmailAndId(user.getEmail(), id);
        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        ResponseMessage responseMessage = new ResponseMessage();


        if (eWasteDrive == null) {
            responseMessage.setStatus(FAIL);
            responseMessage.setData(DRIVE_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        eWasteDrive.setStatus(status);

        LOGGER.info("Edited Drive Summary with id'{}'", eWasteDrive.getId());
        eWasteDriveRepo.save(eWasteDrive);
        LOGGER.info("Updated Drive Summary with id '{}'", eWasteDrive.getId());

        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(eWasteDrive);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for viewing the customer profile
     *
     * @param uid
     * @return Response Entity with status code 200 and display details of customers in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> viewCustomerProfile(String uid) throws BadRequestException, InvalidUserException {
        LOGGER.info("Get Customer Profile :: " + API_HAS_STARTED_SUCCESSFULLY);

        User customer = userRepo.findUserByUid(uid);
        ResponseMessage responseMessage = new ResponseMessage();

        if (customer == null) {
            LOGGER.error(PROFILE_NOT_FOUND);

            responseMessage.setStatus(FAIL);
            responseMessage.setData(PROFILE_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        LOGGER.info("Customer details fetched successfully");

        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(customer);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for analytics (E-waste drives you organized v/s overall E-waste drives in the city)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> getAnalytics(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info(GET_ANALYTICS + " " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        List<EWasteDrive> eWasteDriveList;
        List<EWasteDrive> eWasteDriveCityList;
        try {
            user = userRepo.findUserByEmail(email);
            eWasteDriveList = eWasteDriveRepo.getAllEWasteAnalyticsByCollectorEmail(user.getEmail());
            eWasteDriveCityList = eWasteDriveRepo.findAllByCity(user.getCity());

            HashMap<String, Integer> hm = new HashMap<>();

            hm.put(E_WASTE_DRIVE_COLLECTOR, eWasteDriveList.size());
            hm.put(E_WASTE_DRIVE_CITY, eWasteDriveCityList.size());

            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(hm);

            LOGGER.info(ANALYTICS_FETCHED_SUCCESSFULLY);

            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

    }

    /**
     * This function of service is for analytics (Items that are collected by the collector v/s Items that are sold by the collector)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> getAnalyticsV2(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info(GET_ANALYTICS + " " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        HashMap<String, Integer> hashMap = new HashMap<>();
        try {
            user = userRepo.findUserByEmail(email);
            List<User> collectorList = userRepo.findAllUsersByRoleAndCity(COLLECTOR, user.getCity());

            hashMap.put(TEMP_COLLECTED, 0);
            hashMap.put(LAMPS_COLLECTED, 0);
            hashMap.put(LARGE_EQUIP_COLLECTED, 0);
            hashMap.put(SMALL_EQUIP_COLLECTED, 0);
            hashMap.put(SMALL_IT_COLLECTED, 0);
            hashMap.put(SCREENS_COLLECTED, 0);


            for (int i = 0; i < collectorList.size(); i++) {

                List<Orders> listTemp = ordersRepo.findByCategoryAndCollectorUid(TEMP, collectorList.get(i).getUid());
                hashMap.put(TEMP_COLLECTED, hashMap.get(TEMP_COLLECTED) + listTemp.size());

                List<Orders> listLapms = ordersRepo.findByCategoryAndCollectorUid(LAMPS, collectorList.get(i).getUid());
                hashMap.put(LAMPS_COLLECTED, hashMap.get(LAMPS_COLLECTED) + listLapms.size());

                List<Orders> listLargeEqip = ordersRepo.findByCategoryAndCollectorUid(LARGE_EQUIP, collectorList.get(i).getUid());
                hashMap.put(LARGE_EQUIP_COLLECTED, hashMap.get(LARGE_EQUIP_COLLECTED) + listLargeEqip.size());

                List<Orders> listSmallEquip = ordersRepo.findByCategoryAndCollectorUid(SMALL_EQUIP, collectorList.get(i).getUid());
                hashMap.put(SMALL_EQUIP_COLLECTED, hashMap.get(SMALL_EQUIP_COLLECTED) + listSmallEquip.size());

                List<Orders> listSmallIT = ordersRepo.findByCategoryAndCollectorUid(SMALL_IT, collectorList.get(i).getUid());
                hashMap.put(SMALL_IT_COLLECTED, hashMap.get(SMALL_IT_COLLECTED) + listSmallIT.size());

                List<Orders> listScreens = ordersRepo.findByCategoryAndCollectorUid(SCREENS, collectorList.get(i).getUid());
                hashMap.put(SCREENS_COLLECTED, hashMap.get(SCREENS_COLLECTED) + listScreens.size());

            }

            hashMap.put(TEMP_SELL, 0);
            hashMap.put(LAMPS_SELL, 0);
            hashMap.put(LARGE_EQUIP_SELL, 0);
            hashMap.put(SMALL_EQUIP_SELL, 0);
            hashMap.put(SMALL_IT_SELL, 0);
            hashMap.put(SCREENS_SELL, 0);


            for (int i = 0; i < collectorList.size(); i++) {
                List<SellItems> listTemp = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(TEMP, collectorList.get(i).getUid());
                hashMap.put(TEMP_SELL, hashMap.get(TEMP_SELL) + listTemp.size());

                List<SellItems> listLapms = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(LAMPS, collectorList.get(i).getUid());
                hashMap.put(LAMPS_SELL, hashMap.get(LAMPS_SELL) + listLapms.size());

                List<SellItems> listLargeEquip = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(LARGE_EQUIP, collectorList.get(i).getUid());
                hashMap.put(LARGE_EQUIP_SELL, hashMap.get(LARGE_EQUIP_SELL) + listLargeEquip.size());

                List<SellItems> listSmallEquip = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(SMALL_EQUIP, collectorList.get(i).getUid());
                hashMap.put(SMALL_EQUIP_SELL, hashMap.get(SMALL_EQUIP_SELL) + listSmallEquip.size());

                List<SellItems> listSmallIT = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(SMALL_IT, collectorList.get(i).getUid());
                hashMap.put(SMALL_IT_SELL, hashMap.get(SMALL_IT_SELL) + listSmallIT.size());

                List<SellItems> listScreens = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(SCREENS, collectorList.get(i).getUid());
                hashMap.put(SCREENS_SELL, hashMap.get(SCREENS_SELL) + listScreens.size());

                LOGGER.info(ANALYTICS_FETCHED_SUCCESSFULLY);
            }
        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(hashMap);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for mark notification as read
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> readNotification(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Read Notification :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        List<Notification> list;
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            user = userRepo.findUserByEmail(email);
            list = notificationRepo.findByRoleAndStatusAndCollectorUid("Collector", false, user.getUid());

            for (int i = 0; i < list.size(); i++) {
                Notification notification = list.get(i);
                notification.setStatus(true);
                notificationRepo.save(notification);
            }

            if (list.isEmpty()) {
                responseMessage.setStatus(FAIL);
                responseMessage.setData(NO_NEW_NOTIFICATION);
                return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
            }

        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(list);

        LOGGER.info("Notification Fetched Successfully");

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for analytics (No of Vendors in city Vs Total No of Vendors)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> getAnalyticsV4(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info(GET_ANALYTICS + " " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        List<User> vendorList;
        List<User> vendorListAll;
        HashMap<String, Integer> hashMap = new HashMap<>();

        try {
            user = userRepo.findUserByEmail(email);
            vendorList = userRepo.findAllUsersByRoleAndCity(VENDOR, user.getCity());
            vendorListAll = userRepo.findAllUsersByRole(VENDOR);

            hashMap.put(VENDOR_CITY, vendorList.size());
            hashMap.put(VENDOR_ALL_CITY, vendorListAll.size());

        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(hashMap);

        LOGGER.info(ANALYTICS_FETCHED_SUCCESSFULLY);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }


    /**
     * This function of service is for analytics (No of Customers in city Vs Total No of Customers)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> getAnalyticsV5(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info(GET_ANALYTICS + " " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        List<User> customerList;
        List<User> customerListAll;
        HashMap<String, Integer> hashMap = new HashMap<>();

        try {
            user = userRepo.findUserByEmail(email);
            customerList = userRepo.findAllUsersByRoleAndCity(CUSTOMER, user.getCity());
            customerListAll = userRepo.findAllUsersByRole(CUSTOMER);

            hashMap.put(CUSTOMER_CITY, customerList.size());
            hashMap.put(CUSTOMER_ALL_CITY, customerListAll.size());

        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(hashMap);

        LOGGER.info(ANALYTICS_FETCHED_SUCCESSFULLY);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }


    /**
     * This function of service is for analytics (Items that are collected by the collector v/s Items that are sold by the collector)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> getAnalyticsV6(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info(GET_ANALYTICS + " " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        HashMap<String, Integer> hashMap = new HashMap<>();
        try {
            user = userRepo.findUserByEmail(email);
            List<VendorOrders> vendorOrdersListTemp = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(user.getUid(), TEMP);
            List<VendorOrders> vendorOrdersListSmallIT = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(user.getUid(), SMALL_IT);
            List<VendorOrders> vendorOrdersListLapms = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(user.getUid(), LAMPS);
            List<VendorOrders> vendorOrdersListScreens = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(user.getUid(), SCREENS);
            List<VendorOrders> vendorOrdersListSmallEquip = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(user.getUid(), SMALL_EQUIP);
            List<VendorOrders> vendorOrdersListLargeEqip = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(user.getUid(), LARGE_EQUIP);

            int tempsum = 0;
            int smallitsum = 0;
            int lapmssum = 0;
            int screenssum = 0;
            int smallequip = 0;
            int largeeqip = 0;

            for (int i = 0; i < vendorOrdersListTemp.size(); i++) {
                tempsum = tempsum + Integer.parseInt(vendorOrdersListTemp.get(i).getPrice());
            }
            for (int i = 0; i < vendorOrdersListSmallIT.size(); i++) {
                smallitsum = smallitsum + Integer.parseInt(vendorOrdersListSmallIT.get(i).getPrice());
            }
            for (int i = 0; i < vendorOrdersListLapms.size(); i++) {
                lapmssum = lapmssum + Integer.parseInt(vendorOrdersListLapms.get(i).getPrice());
            }
            for (int i = 0; i < vendorOrdersListScreens.size(); i++) {
                screenssum = screenssum + Integer.parseInt(vendorOrdersListScreens.get(i).getPrice());
            }
            for (int i = 0; i < vendorOrdersListSmallEquip.size(); i++) {
                smallequip = smallequip + Integer.parseInt(vendorOrdersListSmallEquip.get(i).getPrice());
            }
            for (int i = 0; i < vendorOrdersListLargeEqip.size(); i++) {
                largeeqip = largeeqip + Integer.parseInt(vendorOrdersListLargeEqip.get(i).getPrice());
            }
            hashMap.put(TEMP, tempsum);
            hashMap.put(SMALL_IT, smallitsum);
            hashMap.put(LAMPS, lapmssum);
            hashMap.put(SCREENS, screenssum);
            hashMap.put(SMALL_EQUIP, smallequip);
            hashMap.put(LARGE_EQUIP, largeeqip);

        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        LOGGER.info(ANALYTICS_FETCHED_SUCCESSFULLY);


        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setData(hashMap);
        responseMessage.setStatus(SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for modifying items available on sale
     *
     * @param request       HTTPServletRequest
     * @param sellItemDTO which contains fields (itemName, category, quantity, price, status)
     * @return Response Entity with status code 200 and details of modified items on sale
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> editSellItemAvailableSummary(HttpServletRequest request, SellItemDTO sellItemDTO) throws BadRequestException, InvalidUserException {
        LOGGER.info("Edit Sell Item Available Summary :: " + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User user;
        SellItems sellItems;
        ResponseMessage responseMessage = new ResponseMessage();

        try {
            user = userRepo.findUserByEmail(email);
            sellItems = sellItemRepo.findById(sellItemDTO.getId()).get();

            sellItems.setItemName(sellItemDTO.getItemName());
            sellItems.setStatus(sellItemDTO.getStatus());
            sellItems.setPrice(sellItemDTO.getPrice());

            sellItems.setCategory(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(sellItemDTO.getCategory().getCategoryAccepted()));
            sellItems.setCollectorUid(user.getUid());

            int sellItemsQuantity = Integer.parseInt(sellItems.getQuantity());
            int sellItemsQuantityModel = Integer.parseInt(sellItemDTO.getQuantity());
            int total = Integer.parseInt(sellItems.getAvailableQuantity()) + (sellItemsQuantityModel - sellItemsQuantity);
            if (total > 0) {
                sellItems.setAvailableQuantity(String.valueOf(total));
                sellItems.setQuantity(sellItemDTO.getQuantity());
            } else {
                LOGGER.error("Purchased Items can't be greater than available items");
                responseMessage.setStatus(FAIL);
                responseMessage.setData("Value can not be less than zero");
                return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("Edited Item on sale with id '{}'", sellItems.getId());
            sellItemRepo.save(sellItems);
            LOGGER.info("Sell Items saved successfully with id '{}'", sellItems.getId());
        } catch (NullPointerException nullPointerException) {
            LOGGER.error(USER_DOES_NOT_EXIST);
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(sellItems);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}