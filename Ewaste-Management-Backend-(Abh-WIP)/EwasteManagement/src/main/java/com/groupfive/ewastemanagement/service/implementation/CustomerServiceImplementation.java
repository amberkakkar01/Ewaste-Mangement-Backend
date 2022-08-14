package com.groupfive.ewastemanagement.service.implementation;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.*;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.repository.*;
import com.groupfive.ewastemanagement.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.groupfive.ewastemanagement.helper.Constants.*;
import static com.groupfive.ewastemanagement.helper.Util.checkExpiryOfRequest;
import static com.groupfive.ewastemanagement.helper.Util.throwBadRequestException;

@Service
public class CustomerServiceImplementation implements CustomerService {

    private final CategoriesAcceptedRepo categoriesAcceptedRepo;
    private final UserRepo userRepo;
    private final OrdersRepo ordersRepo;
    private final EWasteDriveRepo eWasteDriveRepo;
    private final NotificationRepo notificationRepo;
    private final UserDetailsRepo userDetailsRepo;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImplementation.class);


    @Autowired
    public CustomerServiceImplementation(CategoriesAcceptedRepo categoriesAcceptedRepo, UserRepo userRepo, OrdersRepo ordersRepo, EWasteDriveRepo eWasteDriveRepo, NotificationRepo notificationRepo, UserDetailsRepo userDetailsRepo) {
        this.categoriesAcceptedRepo = categoriesAcceptedRepo;
        this.userRepo = userRepo;
        this.ordersRepo = ordersRepo;
        this.eWasteDriveRepo = eWasteDriveRepo;
        this.notificationRepo = notificationRepo;
        this.userDetailsRepo = userDetailsRepo;
    }


    /**
     * This function of service is for getting list of collectors based on city and categories
     *
     * @param city     string parameter for city
     * @param category string parameter for category
     * @return list of collectors based on city and categories
     */

    @Override
    public List<String> getCollectorBasedOnCity(String city, String category) {

        LOGGER.info("Get collector based on city :: " + API_HAS_STARTED_SUCCESSFULLY);
        List<User> listCity = userRepo.findAllUsersByRoleAndCity(COLLECTOR, city);

        List<User> categoryList = userRepo.findAllUsersByCat(category);

        List<String> list = new ArrayList<>();
        for (User value : categoryList) {
            list.add(value.getUid());
        }

        List<String> list1 = new ArrayList<>();

        for (User user : listCity) {
            list1.add(user.getUid());
        }

        list1.retainAll(list);

        LOGGER.info("Collector details based on city " + FETCHED_SUCCESSFULLY);
        return list1;

    }

    /**
     * This function of service provides list of all the requests/orders of customer
     *
     * @param pageNo   Integer Parameter that requests the page Number in Request Param
     * @param pageSize Integer Parameter that requests the page Size in Request Param
     * @param request  HTTPServletRequest
     * @return Response Entity with status code 200 and  list of all the requests/orders of customer in body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessageWithPagination> getAllOrders(int pageNo, int pageSize, HttpServletRequest request) throws BadRequestException, InvalidUserException {

        LOGGER.info("Get all order :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(Constants.EMAIL);

        throwBadRequestException(email);

        User customer;
        List<Orders> list;
        try {
            customer = userRepo.findUserByEmail(email);
            Page<Orders> orders = ordersRepo.findAllByCustomerUid(customer.getUid(), PageRequest.of(pageNo - 1, pageSize));
            list = orders.getContent();

        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        ResponseMessageWithPagination responseMessageWithPagination = new ResponseMessageWithPagination();

        if (list.isEmpty()) {
            responseMessageWithPagination.setStatus(Constants.FAIL);
            responseMessageWithPagination.setData(NO_ORDERS);

            return new ResponseEntity<>(responseMessageWithPagination, HttpStatus.BAD_REQUEST);
        }

        responseMessageWithPagination.setStatus(SUCCESS);
        responseMessageWithPagination.setPageNo(pageNo);
        responseMessageWithPagination.setPageSize(pageSize);
        responseMessageWithPagination.setData(list);
        responseMessageWithPagination.setTotalRecords(list.size());

        LOGGER.info("All Orders " + FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(responseMessageWithPagination, HttpStatus.OK);
    }

    /**
     * This function of service provides details of E-Waste drive organized in the city
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and list of E-Waste drives in the city in body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> viewDrives(HttpServletRequest request) throws BadRequestException, InvalidUserException {

        LOGGER.info("Get details of drive for customer :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);


        User customer;
        List<EWasteDrive> listDrive;
        List<EWasteDrive> listDriveCity;
        try {
            customer = userRepo.findUserByEmail(email);
            listDrive = eWasteDriveRepo.findByStatus(Constants.UPCOMING);
            LOGGER.info("Fetching all upcoming drives");

            listDriveCity = eWasteDriveRepo.findAllByCity(customer.getCity());
            LOGGER.info("Fetching all drives organized in the customer city");
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        listDrive.retainAll(listDriveCity);

        for (EWasteDrive eWaste : listDrive) {
            String date = eWaste.getDate();

            if (checkExpiryOfRequest(date)) {
                LOGGER.info("Checking Expiry Status of Drive organized");
                eWaste.setStatus(Constants.COMPLETED);
                eWasteDriveRepo.save(eWaste);
            }
        }
        ResponseMessage responseMessage = new ResponseMessage();

        if (listDrive.isEmpty()) {
            LOGGER.error(NO_DRIVES_AVAILABLE);
            responseMessage.setStatus(Constants.FAIL);
            responseMessage.setData("No Drives In your Area");

            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(listDrive);

        LOGGER.info("Drive details " + FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }

    /**
     * This function of service provides list of un-read notification
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and list of un-read notifications in body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> viewNotification(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        LOGGER.info("Get notification :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);


        User customer;
        List<Notification> notifications;
        try {
            customer = userRepo.findUserByEmail(email);
            notifications = notificationRepo.findByRoleAndStatusAndCustomerUid("Customer", false, customer.getUid());

        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        ResponseMessage responseMessage = new ResponseMessage();

        if (notifications.isEmpty()) {
            responseMessage.setStatus(Constants.FAIL);
            responseMessage.setData(NO_NEW_NOTIFICATION);
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(notifications);

        LOGGER.info("Notifications " + FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseMessage> viewCollectorProfile(String uid) {

        LOGGER.info("Get collector profile :: " + API_HAS_STARTED_SUCCESSFULLY);
        User collector = userRepo.findUserByUid(uid);

        ResponseMessage responseMessage = new ResponseMessage();
        if (collector == null) {
            responseMessage.setStatus(FAIL);
            responseMessage.setData(ORDER_NOT_ACCEPTED_BY_COLLECTOR);
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }

        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(collector);

        LOGGER.info("View collector profile " + FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service marks un-read notifications as viewed
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and viewed notifications in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> readNotification(HttpServletRequest request) throws BadRequestException, InvalidUserException {

        LOGGER.info("Read Notification :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);


        User customer;
        List<Notification> notificationList;
        try {
            customer = userRepo.findUserByEmail(email);
            notificationList = notificationRepo.findByRoleAndStatusAndCustomerUid("Customer", false, customer.getUid());

        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        for (Notification customerNotification : notificationList) {
            customerNotification.setStatus(true);
            notificationRepo.save(customerNotification);
        }
        ResponseMessage responseMessage = new ResponseMessage();

        if (notificationList.isEmpty()) {
            responseMessage.setStatus(FAIL);
            responseMessage.setData(NO_UNREAD_NOTIFICATION);
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }

        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(notificationList);

        LOGGER.info("Notification " + FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for analytics (Total Orders in City VS Orders Placed By The Customer)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */
    @Override
    public ResponseEntity<ResponseMessage> getAnalytics(HttpServletRequest request) {

        LOGGER.info(GET_ANALYTICS + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User customer;
        List<Orders> orderInCity;
        List<Orders> orderCustomer;
        HashMap<String, Integer> hashMap = new HashMap<>();
        try {
            customer = userRepo.findUserByEmail(email);
            orderInCity = ordersRepo.findAllByCity(customer.getCity());
            orderCustomer = ordersRepo.findAllAnalyticsByCustomerUid(customer.getUid());
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        hashMap.put(ORDER_IN_CITY, orderInCity.size());
        hashMap.put(ORDER_CUSTOMER, orderCustomer.size());

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setData(hashMap);
        responseMessage.setStatus(SUCCESS);

        LOGGER.info(ANALYTICS + FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for analytics (E-Waste Drive Organized in City VS Total E-Waste Drive Scheduled)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */
    @Override
    public ResponseEntity<ResponseMessage> getAnalyticsV2(HttpServletRequest request) {
        LOGGER.info(GET_ANALYTICS + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        throwBadRequestException(email);

        User customer;
        List<EWasteDrive> eWasteDriveListCity;
        List<EWasteDrive> eWasteDriveListAll;
        HashMap<String, Integer> hashMap = new HashMap<>();
        try {
            customer = userRepo.findUserByEmail(email);
            eWasteDriveListCity = eWasteDriveRepo.findAllByCity(customer.getCity());
            eWasteDriveListAll = eWasteDriveRepo.findAll();
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
        hashMap.put(E_WASTE_DRIVE_LIST_CITY, eWasteDriveListCity.size());
        hashMap.put(E_WASTE_DRIVE_LIST_ALL, eWasteDriveListAll.size());

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setData(hashMap);
        responseMessage.setStatus(SUCCESS);

        LOGGER.info(ANALYTICS + FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }

    /**
     * This function of service is for analytics (Collectors in city based on categories Accepted)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> getAnalyticsForCollectorInCity(HttpServletRequest request) {
        LOGGER.info(GET_ANALYTICS + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(Constants.EMAIL);

        throwBadRequestException(email);

        User customer;

        HashMap<String, Integer> hashMap = new HashMap<>();
        try {
            customer = userRepo.findUserByEmail(email);
            List<User> collectorList = userRepo.findAllUsersByRoleAndCity(COLLECTOR, customer.getCity());
            List<UserDetails> collectorDetailsList = new ArrayList<>();
            for (User user : collectorList) {
                String uid = user.getUid();
                collectorDetailsList.add(userDetailsRepo.findUserByUid(uid));
            }
            hashMap.put(TEMP_CITY, 0);
            hashMap.put(LAMPS_CITY, 0);
            hashMap.put(LARGE_EQUIP_CITY, 0);
            hashMap.put(SMALL_EQUIP_CITY, 0);
            hashMap.put(SMALL_IT_CITY, 0);
            hashMap.put(SCREENS_CITY, 0);

            for (UserDetails details : collectorDetailsList) {
                List<CategoriesAccepted> listTemp = categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, details.getId());
                hashMap.put(TEMP_CITY, hashMap.get(TEMP_CITY) + listTemp.size());

                List<CategoriesAccepted> listLamps = categoriesAcceptedRepo.findAllByCategoryAccepted(LAMPS, details.getId());
                hashMap.put(LAMPS_CITY, hashMap.get(LAMPS_CITY) + listLamps.size());

                List<CategoriesAccepted> listLargeEquip = categoriesAcceptedRepo.findAllByCategoryAccepted(LARGE_EQUIP, details.getId());
                hashMap.put(LARGE_EQUIP_CITY, hashMap.get(LARGE_EQUIP_CITY) + listLargeEquip.size());

                List<CategoriesAccepted> listSmallEquip = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_EQUIP, details.getId());
                hashMap.put(SMALL_EQUIP_CITY, hashMap.get(SMALL_EQUIP_CITY) + listSmallEquip.size());

                List<CategoriesAccepted> listSmallIT = categoriesAcceptedRepo.findAllByCategoryAccepted("SmallIT", details.getId());
                hashMap.put(SMALL_IT_CITY, hashMap.get(SMALL_IT_CITY) + listSmallIT.size());

                List<CategoriesAccepted> listScreens = categoriesAcceptedRepo.findAllByCategoryAccepted(SCREENS, details.getId());
                hashMap.put(SCREENS_CITY, hashMap.get(SCREENS_CITY) + listScreens.size());
            }

        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(SUCCESS);

        responseMessage.setData(hashMap);

        LOGGER.info(ANALYTICS + FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseMessage> getAnalyticsForAllCollector(HttpServletRequest request) {
        LOGGER.info(GET_ANALYTICS + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(Constants.EMAIL);

        throwBadRequestException(email);

        HashMap<String, Integer> hashMap = new HashMap<>();
        try {
            List<User> collectorTotalList = userRepo.findAllUsersByRole(COLLECTOR);
            List<UserDetails> collectorTotalDetailsList = new ArrayList<>();
            for (User user : collectorTotalList) {
                String uid = user.getUid();
                collectorTotalDetailsList.add(userDetailsRepo.findUserByUid(uid));
            }
            hashMap.put(TEMP_TOTAL, 0);
            hashMap.put(LAMPS_TOTAL, 0);
            hashMap.put(LARGE_EQUIP_TOTAL, 0);
            hashMap.put(SMALL_EQUIP_TOTAL, 0);
            hashMap.put(SMALL_IT_TOTAL, 0);
            hashMap.put(SCREENS_TOTAL, 0);

            for (UserDetails userDetails : collectorTotalDetailsList) {
                List<CategoriesAccepted> listTemp = categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, userDetails.getId());
                hashMap.put(TEMP_TOTAL, hashMap.get(TEMP_TOTAL) + listTemp.size());

                List<CategoriesAccepted> listLamps = categoriesAcceptedRepo.findAllByCategoryAccepted(LAMPS, userDetails.getId());
                hashMap.put(LAMPS_TOTAL, hashMap.get(LAMPS_TOTAL) + listLamps.size());

                List<CategoriesAccepted> listLargeEquip = categoriesAcceptedRepo.findAllByCategoryAccepted(LARGE_EQUIP, userDetails.getId());
                hashMap.put(LARGE_EQUIP_TOTAL, hashMap.get(LARGE_EQUIP_TOTAL) + listLargeEquip.size());

                List<CategoriesAccepted> listSmallEquip = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_EQUIP, userDetails.getId());
                hashMap.put(SMALL_EQUIP_TOTAL, hashMap.get(SMALL_EQUIP_TOTAL) + listSmallEquip.size());

                List<CategoriesAccepted> listSmallIT = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_IT, userDetails.getId());
                hashMap.put(SMALL_IT_TOTAL, hashMap.get(SMALL_IT_TOTAL) + listSmallIT.size());

                List<CategoriesAccepted> listScreens = categoriesAcceptedRepo.findAllByCategoryAccepted(SCREENS, userDetails.getId());
                hashMap.put(SCREENS_TOTAL, hashMap.get(SCREENS_TOTAL) + listScreens.size());
            }
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(SUCCESS);

        responseMessage.setData(hashMap);

        LOGGER.info(ANALYTICS + FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}