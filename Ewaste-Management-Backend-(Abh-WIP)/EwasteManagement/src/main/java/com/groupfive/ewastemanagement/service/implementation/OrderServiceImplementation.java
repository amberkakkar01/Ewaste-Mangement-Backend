package com.groupfive.ewastemanagement.service.implementation;

import com.groupfive.ewastemanagement.dto.request.RequestDTO;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.entity.*;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.repository.*;
import com.groupfive.ewastemanagement.service.CustomerService;
import com.groupfive.ewastemanagement.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final OrdersRepo ordersRepo;
    private final UserRepo userRepo;
    private final CustomerService checkService;
    private final AllPendingRequestRepo allPendingRequestRepo;
    private final CategoriesAcceptedRepo categoriesAcceptedRepo;
    private final NotificationRepo notificationRepo;
    private final UserDetailsRepo userDetailsRepo;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImplementation.class);

    @Autowired
    public OrderServiceImplementation(OrdersRepo ordersRepo, UserRepo userRepo, CustomerService checkService, AllPendingRequestRepo allPendingRequestRepo, CategoriesAcceptedRepo categoriesAcceptedRepo, NotificationRepo notificationRepo, UserDetailsRepo userDetailsRepo) {
        this.ordersRepo = ordersRepo;
        this.userRepo = userRepo;
        this.checkService = checkService;
        this.allPendingRequestRepo = allPendingRequestRepo;
        this.categoriesAcceptedRepo = categoriesAcceptedRepo;
        this.notificationRepo = notificationRepo;
        this.userDetailsRepo = userDetailsRepo;
    }

    public void throwBadRequestException(String email) {
        if (email == null) {
            LOGGER.error(Constants.EMPTY_EMAIL);
            throw new BadRequestException(EMAIL_CANNOT_BE_EMPTY);
        }
    }

    public List<User> returnListOfUser(String email) {
        User customer;
        List<User> list;

        customer = userRepo.findUserByEmail(email);//cookies
        String city = customer.getCity();
        list = userRepo.findAllUsersByRoleAndCity(COLLECTOR, city);
        return list;

    }

    public List<UserDetails> returnListOfUserDetails(String category) {
        CategoriesAccepted categoriesAccepted = categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(category);
        return userDetailsRepo.findUserDetailsByCategoriesAcceptedId(categoriesAccepted.getId());
    }

    /**
     * This function of the service create a pickup request for customer to sell E-Waste
     *
     * @param requestDTO which contains fields (CategoriesAccepted, quantity, requestType, scheduleDate, scheduledTime, itemName)
     * @param request      HTTPServletRequest
     * @return Response Entity with status code 201 and details of order in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */
    @Override
    public ResponseEntity<ResponseMessage> createPickUpRequest(RequestDTO requestDTO, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Get Pickup Request :: " + API_HAS_STARTED_SUCCESSFULLY);
        ResponseMessage responseMessage = new ResponseMessage();
        Orders orders = new Orders();

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);


        try {
            User customer = userRepo.findUserByEmail(email);
            LOGGER.info("A pickup request for '{}' is made by user with id '{}'", requestDTO.getItemName(),
                    customer.getId());
            List<String> uidList;
            uidList = checkService.getCollectorBasedOnCity(customer.getCity(), requestDTO.getCategory().getCategoryAccepted());

            if (uidList.isEmpty()) {
                LOGGER.error(NO_COLLECTOR_FOUND);
                responseMessage.setStatus(FAIL);
                responseMessage.setData(NO_COLLECTOR_FOUND);
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }

            CategoriesAccepted categoriesAccepted = categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(requestDTO.getCategory().getCategoryAccepted());
            orders.setCategory(categoriesAccepted);
            orders.setQuantity(requestDTO.getQuantity());
            orders.setUser(customer);
            orders.setRequestType(PICK_UP);

            orders.setCustomerUid(customer.getUid());

            orders.setStatus(PENDING);

            orders.setOrderUid(orders.getOrderUid());
            orders.setItemName(requestDTO.getItemName());
            orders.setScheduledDate(requestDTO.getScheduledDate());
            orders.setScheduledTime(requestDTO.getScheduledTime());

            for (String s : uidList) {
                AllPendingRequest pendingRequest = new AllPendingRequest();

                pendingRequest.setOrderId(orders.getOrderUid());
                pendingRequest.setCollectorUid(s);

                pendingRequest.setStatus(PENDING);

                pendingRequest.setCategory(categoriesAccepted);
                pendingRequest.setQuantity(orders.getQuantity());
                pendingRequest.setRequestType(orders.getRequestType());
                pendingRequest.setAddress(orders.getUser().getAddress1() + ", " + orders.getUser().getCity() + ", " + orders.getUser().getState());
                pendingRequest.setScheduleDate(orders.getScheduledDate());
                pendingRequest.setScheduledTime(orders.getScheduledTime());
                pendingRequest.setItemName(orders.getItemName());

                allPendingRequestRepo.save(pendingRequest);

                LOGGER.info("A pickup request for '{}' is send to collector with id '{}'", requestDTO.getItemName(),
                        s);

                Notification notification = new Notification();
                notification.setCollectorUid(s);
                notification.setRole("Collector");
                notification.setMessage("One Pick-Up Request is Pending");
                notification.setStatus(false);
                notificationRepo.save(notification);

            }
            ordersRepo.save(orders);

            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(orders);

            LOGGER.info("Get Pickup Request :: " + "Pickup Request scheduled successfully");
            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);

        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }


    }

    /**
     * This function of the service provides the count of nearby drop off location for customer
     *
     * @param category String parameter
     * @param request  HTTPServletRequest
     * @return Response Entity with status code 200 and count of drop-off location nearby
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */
    @Override
    public int showDropOffLocation(String category, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Count of drop-off :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User customer;
        List<User> list;
        try {
            customer = userRepo.findUserByEmail(email);
            String city = customer.getCity();
            list = userRepo.findAllUsersByRoleAndCity(COLLECTOR, city);
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        CategoriesAccepted categoriesAccepted = categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(category);
        List<UserDetails> categoryList = userDetailsRepo.findUserDetailsByCategoriesAcceptedId(categoriesAccepted.getId());

        List<User> catListCollector = new ArrayList<>();
        for (UserDetails s : categoryList) {
            catListCollector.add(userRepo.findUserByUid(s.getUid()));
        }

        list.retainAll(catListCollector);

        LOGGER.info("Count of drop-off location " + FETCHED_SUCCESSFULLY);
        return list.size();
    }

    /**
     * This function of the service provides list of collector available for drop-off nearby his location
     *
     * @param category string parameter
     * @param request  HTTPServletRequest
     * @return Response Entity with status code 200 and list of collector available in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */
    @Override
    public ResponseEntity<ResponseMessage> viewListOfCollectorsForDropOff(String category, HttpServletRequest request) throws BadRequestException, InvalidUserException {

        LOGGER.info("Get list of collector for drop-off :: " + API_HAS_STARTED_SUCCESSFULLY);
        ResponseMessage responseMessage = new ResponseMessage();

        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        try {
            List<User> list;

            list = returnListOfUser(email);

            List<UserDetails> categoryList = returnListOfUserDetails(category);

            List<User> catListCollector = new ArrayList<>();
            for (UserDetails s : categoryList) {
                catListCollector.add(userRepo.findUserByUid(s.getUid()));
            }

            list.retainAll(catListCollector);

            if (list.isEmpty()) {
                responseMessage.setStatus(FAIL);
                responseMessage.setData("No Collector in your area accepting entered category");
                return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
            }

            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(list);

            LOGGER.info("List of collector for drop-off " + FETCHED_SUCCESSFULLY);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
    }

    /**
     * This function of the service provides the count of collector that are available for pickUp Service
     *
     * @param category string parameter
     * @param request  HTTPServletRequest
     * @return count of collectors available based on category
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */
    @Override
    public int countCollectorPickUp(String category, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Get count of collector :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        List<User> list;
        try {
            list = returnListOfUser(email);

            List<UserDetails> categoryList = returnListOfUserDetails(category);

            List<String> categoriesList = new ArrayList<>();
            for (UserDetails userDetails : categoryList) {
                categoriesList.add(userDetails.getUid());
            }

            List<String> cityList = new ArrayList<>();
            for (User user : list) {
                cityList.add(user.getUid());
            }

            categoriesList.retainAll(cityList);

            LOGGER.info("Count collector api " + FETCHED_SUCCESSFULLY);
            return categoryList.size();
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
    }


    /**
     * This function of the service creates the drop-off request to sell E-Waste
     *
     * @param requestDTO which contains fields (CategoriesAccepted, quantity, requestType, scheduleDate, scheduledTime, itemName)
     * @param request      HTTPServletRequest
     * @return Response Entity with status code 201 and details of order created in body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */
    @Override
    public ResponseEntity<ResponseMessage> createDropOffRequest(RequestDTO requestDTO, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Get Drop-off Request :: " + API_HAS_STARTED_SUCCESSFULLY);
        ResponseMessage responseMessage = new ResponseMessage();
        Orders orders = new Orders();
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User customer;
        try {
            customer = userRepo.findUserByEmail(email);
            orders.setCategory(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(requestDTO.getCategory().getCategoryAccepted()));
            orders.setQuantity(requestDTO.getQuantity());
            orders.setRequestType(DROP_OFF);
            orders.setUser(customer);

            orders.setCustomerUid(customer.getUid());
            orders.setStatus(PENDING);

            orders.setOrderUid(orders.getOrderUid());
            orders.setItemName(requestDTO.getItemName());
            orders.setScheduledDate(requestDTO.getScheduledDate());
            orders.setScheduledTime(requestDTO.getScheduledTime());

            orders.setCollectorUid(requestDTO.getCollectorUid());

            ordersRepo.save(orders);
            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(orders);

            LOGGER.info("Drop-off request saved successfully");
            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

    }
}