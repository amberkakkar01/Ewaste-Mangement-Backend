package com.groupfive.ewastemanagement.controller;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.RequestDTO;
import com.groupfive.ewastemanagement.service.CustomerService;
import com.groupfive.ewastemanagement.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.groupfive.ewastemanagement.helper.Constants.AUTHORIZATION;
import static com.groupfive.ewastemanagement.helper.Constants.USER_WITH_ID_VIEWED_HIS_ANALYTICS;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    private final OrderService orderService;
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    @Autowired
    public CustomerController(OrderService orderService, CustomerService customerService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * This controller is for handling the API calls to create a pickup request for customer order
     *
     * @param requestDTO which contains fields (CategoriesAccepted, quantity, requestType, scheduleDate, scheduledTime, itemName)
     * @param request      HTTPServletRequest
     * @return Response Entity with status code 201 and details of order in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @PostMapping(value = "/pick-up", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "This controller is for handling the API calls to create a pickup request for customer order")
    public ResponseEntity<ResponseMessage> createPickUpRequest(@Valid @RequestBody RequestDTO requestDTO, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is requesting pickup request", id);
        return orderService.createPickUpRequest(requestDTO, request);
    }

    /**
     * This controller is for handling the API calls to create a dropOff request for customer order
     *
     * @param requestDTO which contains fields (CategoriesAccepted, quantity, requestType, scheduleDate, scheduledTime, itemName)
     * @param request      HTTPServletRequest
     * @return Response Entity with status code 201 and details of order in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @PostMapping(value = "/drop-off", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "This controller is for handling the API calls to create a dropOff request for customer order")
    public ResponseEntity<ResponseMessage> createDropOffRequest(@Valid @RequestBody RequestDTO requestDTO, HttpServletRequest request) throws BadRequestException, InvalidUserException {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is requesting drop-off request", id);
        return orderService.createDropOffRequest(requestDTO, request);
    }

    /**
     * This controller is for handling the API calls to count nearby drop off location for customer
     *
     * @param category String parameter
     * @param request  HTTPServletRequest
     * @return Response Entity with status code 200 and count of drop-off location nearby
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @GetMapping(value = "/drop-off/details", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to count nearby drop off location for customer")
    public int searchDropOffLocation(@RequestParam(name = "category") String category, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching count of collector for drop-off request", id);
        return orderService.showDropOffLocation(category, request);
    }

    /**
     * This controller is for handling the API calls to display list of collector accepting Drop-Off
     *
     * @param category String parameter
     * @param request  HTTPServletRequest
     * @return Response Entity with status code 200 and list of collector accepting drop-off requests nearby
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @GetMapping(value = "/drop-off/view-collectors", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to display list of collector accepting Drop-Off")
    public ResponseEntity<ResponseMessage> viewListOfCollectorsForDropOff(@RequestParam(name = "category") String category, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching details of collectors available for drop-off", id);
        return orderService.viewListOfCollectorsForDropOff(category, request);
    }

    /**
     * This controller is for handling the API calls to display list of collector accepting Pick-Up Request
     *
     * @param category String parameter
     * @param request  HTTPServletRequest
     * @return Response Entity with status code 200 and count of collector accepting pick-up requests nearby
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @GetMapping(value = "/pick-up/view-collectors", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to display list of collector accepting Pick-Up Request")
    public int countCollectorPickUp(@RequestParam(name = "category") String category, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching count of collector available for pickup in his nearby area", id);
        return orderService.countCollectorPickUp(category, request);
    }

    /**
     * This controller is for handling the API calls to display list of all the requests/orders of customer
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and list of all the requests/orders of customer in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @GetMapping(value = "/request/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to display list of all the requests/orders of customer")
    public ResponseEntity<ResponseMessageWithPagination> getAllOrders(@RequestParam(name = "pageNo") int pageNo, @RequestParam("pageSize") int pageSize, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching all his request/orders", id);
        return customerService.getAllOrders(pageNo, pageSize, request);
    }

    /**
     * This controller is for handling the API calls to display list of E-Waste drive organized in the city
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and list of E-Waste drive organized in the city in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @GetMapping(value = "/view-drives", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to display list of E-Waste drive organized in the city")
    public ResponseEntity<ResponseMessage> viewEWasteDrives(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching all E-waste drives  details scheduled in city", id);
        return customerService.viewDrives(request);
    }

    /**
     * This controller is for handling the API calls to display list of unread notification
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and list of unread notifications in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @GetMapping(value = "/customer/notification", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to display list of unread notification")
    public ResponseEntity<ResponseMessage> viewNotification(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching all unread notification", id);
        return customerService.viewNotification(request);
    }

    /**
     * This controller is for handling the API calls to mark notification as read
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and details notification in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @PostMapping(value = "/customer/notification/mark-as-read", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to mark notification as read")
    public ResponseEntity<ResponseMessage> readNotification(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is marking all notification as read", id);
        return customerService.readNotification(request);
    }

    /**
     * This controller is for handling the API calls to view collector's profile
     *
     * @param uid String Parameter
     * @return Response Entity with status code 200 and details of collector in the body
     */

    @GetMapping(value = "/request/all/collector-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view collector's profile")
    public ResponseEntity<ResponseMessage> viewCollectorProfile(@RequestParam(name = "user_id") String uid) {
        return customerService.viewCollectorProfile(uid);
    }

    /**
     * This controller is for handling the API calls to view Analytics(No of E-Waste Drives in your City v/s Total number of E-Waste Drives)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/customer/analytics/view-drives", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Analytics(No of E-Waste Drives in your City v/s Total number of E-Waste Drives)")
    public ResponseEntity<ResponseMessage> getAnalyticsV1(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return customerService.getAnalytics(request);
    }

    /**
     * This controller is for handling the API calls to view Analytics(E-Waste generated in your city v/s Total E-Waste you donated)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/customer/analytics/view-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Analytics(E-Waste generated in your city v/s Total E-Waste you donated)")
    public ResponseEntity<ResponseMessage> getAnalyticsV2(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return customerService.getAnalyticsV2(request);
    }

    /**
     * This controller is for handling the API calls to view Analytics(Collector’s in your city with categories they accept VS Total No of Collectors)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/customer/analytics/view-collector", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Analytics(Collector’s in your city with categories they accept VS Total No of Collectors)\n")
    public ResponseEntity<ResponseMessage> getAnalyticsV3(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return customerService.getAnalyticsForCollectorInCity(request);
    }
}