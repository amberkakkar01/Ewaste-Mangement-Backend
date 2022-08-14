package com.groupfive.ewastemanagement.controller;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.EWasteDriveDTO;
import com.groupfive.ewastemanagement.dto.request.SellItemDTO;
import com.groupfive.ewastemanagement.service.CollectorService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.groupfive.ewastemanagement.helper.Constants.AUTHORIZATION;
import static com.groupfive.ewastemanagement.helper.Constants.USER_WITH_ID_VIEWED_HIS_ANALYTICS;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAuthority('COLLECTOR')")
@Validated
public class CollectorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorController.class);
    private final CollectorService collectorService;
    private final JwtUtil jwtUtil;

    @Autowired
    public CollectorController(CollectorService collectorService, JwtUtil jwtUtil) {
        this.collectorService = collectorService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * This controller is for handling the API calls to get all the pending request of collector
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and all pending requests in body
     */

    @GetMapping(value = "/request/pending", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to get all the pending request of collector")
    public ResponseEntity<ResponseMessageWithPagination> getPendingRequest(@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching all the pending requests", id);
        return collectorService.getPendingRequest(pageNo, pageSize, request);
    }

    /**
     * This controller is for handling the API calls to get all requests summary of collector
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and all requests summary of collector in body
     */

    @GetMapping(value = "/request/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to get all requests summary of collector")
    public ResponseEntity<ResponseMessageWithPagination> getRequestSummary(@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize, HttpServletRequest request)//@RequestBody PendingRequest pendingRequest)
    {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching the all his past request", id);
        return collectorService.getRequestSummary(pageNo, pageSize, request);
    }

    /**
     * This controller is for handling the API calls to accept pending request of collector
     *
     * @param orderId String Parameter
     * @param request String Parameter
     * @return Response Entity with status code 200 and details of accepted requests in body
     */

    @PostMapping(value = "/request/accept-pending", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = " This controller is for handling the API calls to accept pending request of collector")
    public ResponseEntity<ResponseMessage> acceptPendingRequest(@RequestParam(name = "order") String orderId, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is accepting the pending request with order id '{}'", id, orderId);
        return collectorService.acceptPendingRequest(orderId, request);
    }

    /**
     * This controller is for handling the API calls to organize E-Waste Drives
     *
     * @param eWasteDriveDTO which contains fields (driveName, categoryAccepted, date, time, status)
     * @param request          String parameter
     * @return Response Entity with status code 201 and with all details of organized E-Waste drive in the body
     */

    @PostMapping(value = "/organize-drive", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "This controller is for handling the API calls to organize E-Waste Drives")
    public ResponseEntity<ResponseMessage> organizeDrive(@Valid @RequestBody EWasteDriveDTO eWasteDriveDTO, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is organizing E-Waste Drive in his locality", id);
        return collectorService.organizeDrive(eWasteDriveDTO, request);
    }

    /**
     * This controller is for handling the API calls to view all organized E-Waste Drives
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and with list of all the organized E-Waste drive in the body
     */

    @GetMapping(value = "/my-drive", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view all organized E-Waste Drives")
    public ResponseEntity<ResponseMessageWithPagination> viewMyDrive(@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching the all his E-Waste drives", id);
        return collectorService.viewMyDrive(pageNo, pageSize, request);
    }

    /**
     * This controller is for handling the API calls to sell E-Waste
     *
     * @param sellItemDTO which contains fields (itemName, category, quantity, price, status)
     * @param request       HTTPServletRequest
     * @return Response Entity with status code 201 and details of item put on sale in the body
     */

    @PostMapping(value = "/sell", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "This controller is for handling the API calls to sell E-Waste")
    public ResponseEntity<ResponseMessage> sellItem(@Valid @RequestBody SellItemDTO sellItemDTO, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is putting E-Waste items on sale", id);
        return collectorService.sellItem(sellItemDTO, request);
    }

    /**
     * This controller is for handling the API calls to view item available on sale
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and list of items available on sale in the body
     */

    @GetMapping(value = "/available/sell-summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view item available on sale")
    public ResponseEntity<ResponseMessage> sellItemAvailableSummary(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching all the available items placed on sale", id);
        return collectorService.sellItemAvailableSummary(request);
    }

    /**
     * This controller is for handling the API calls to view item purchased by vendor
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays the list of items purchased by vendor in the body
     */

    @GetMapping(value = "/sold/sell-summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view item purchased by vendor")
    public ResponseEntity<ResponseMessage> sellItemSoldSummary(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching all the sold items placed on sale", id);
        return collectorService.sellItemSoldSummary(request);
    }

    /**
     * This controller is for handling the API calls to modify items available on sale
     *
     * @param request       HTTPServletRequest
     * @param sellItemDTO which contains fields (itemName, category, quantity, price, status)
     * @return Response Entity with status code 200 and details of modified items on sale
     */

    @PutMapping(value = "/available/sell-summary", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to modify items available on sale")
    public ResponseEntity<ResponseMessage> editSellItemAvailableSummary(HttpServletRequest request, @Valid @RequestBody SellItemDTO sellItemDTO) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' changing status available/sold for items on sale", id);
        return collectorService.editSellItemAvailableSummary(request, sellItemDTO);
    }

    /**
     * This controller is for handling the API calls to view unread notification
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays the unread notification in the body
     */

    @GetMapping(value = "/collector/notification", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view unread notification")
    public ResponseEntity<ResponseMessage> viewNotification(HttpServletRequest request) {
        String googleId = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching his notifications", googleId);
        return collectorService.viewNotification(request);
    }

    /**
     * This controller is for handling the API calls to mark notification as read
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays the read notifications in the body
     */

    @PostMapping(value = "/collector/notification/mark-as-read", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to mark notification as read")
    public ResponseEntity<ResponseMessage> readNotification(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' marked the notifications as read", id);
        return collectorService.readNotification(request);
    }

    /**
     * This controller is for handling the API calls to modify status of E-Waste Drives Scheduled
     *
     * @param id      Long Parameter
     * @param status  String Parameter for changing status
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays modified profile details
     */

    @PutMapping(value = "/my-drive", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to modify status of E-Waste Drives Scheduled")
    public ResponseEntity<ResponseMessage> editDriveSummary(@RequestParam(name = "id") Long id, @RequestParam(name = "status") String status, HttpServletRequest request) {
        String fetchId = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' changing status scheduled/completed for E-Waste Drives", fetchId);
        return collectorService.editDriveSummary(id, status, request);
    }

    /**
     * This controller is for handling the API calls to view Customer Profile
     *
     * @param uid String Parameter
     * @return Response Entity with status code 200 and displays customer profile details
     */

    @GetMapping(value = "/view/customer-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Customer Profile ")
    public ResponseEntity<ResponseMessage> viewCustomerProfile(@RequestParam(name = "id") String uid) {
        return collectorService.viewCustomerProfile(uid);
    }

    /**
     * This controller is for handling the API calls to view Analytics (E-waste drives you organized v/s overall E-waste drives in the city)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/collector/analytics/e-waste-drives", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Analytics (E-waste drives you organized v/s overall E-waste drives in the city)")
    public ResponseEntity<ResponseMessage> getAnalyticsV1(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return collectorService.getAnalytics(request);
    }

    /**
     * This controller is for handling the API calls to view Analytics (Items that are collected by the collector v/s Items that are sold by the collector)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/collector/analytics/category-accepted", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Analytics (Items that are collected by the collector v/s Items that are sold by the collector)")
    public ResponseEntity<ResponseMessage> getAnalyticsV2(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return collectorService.getAnalyticsV2(request);
    }

    /**
     * This controller is for handling the API calls to view Analytics (No of Vendors in city Vs Total No of Vendors)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/collector/analytics/view-vendors", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Analytics (No of Vendors in city Vs Total No of Vendors)")
    public ResponseEntity<ResponseMessage> getAnalyticsV4(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return collectorService.getAnalyticsV4(request);
    }

    /**
     * This controller is for handling the API calls to view Analytics (No of Customers in city Vs Total No of Customers)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/collector/analytics/view-customer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Analytics (No of Customers in city Vs Total No of Customers)")
    public ResponseEntity<ResponseMessage> getAnalyticsV5(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return collectorService.getAnalyticsV5(request);
    }

    /**
     * This controller is for handling the API calls to view Analytics (Items that are collected by the collector v/s Items that are sold by the collector)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/collector/analytics/view-collector", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Analytics (Items that are collected by the collector v/s Items that are sold by the collector)")
    public ResponseEntity<ResponseMessage> getAnalyticsV6(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return collectorService.getAnalyticsV6(request);
    }


}