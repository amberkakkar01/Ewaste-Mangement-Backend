package com.groupfive.ewastemanagement.controller;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.dto.request.AcceptItemOnSaleDTO;
import com.groupfive.ewastemanagement.service.VendorService;
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

import static com.groupfive.ewastemanagement.helper.Constants.AUTHORIZATION;
import static com.groupfive.ewastemanagement.helper.Constants.USER_WITH_ID_VIEWED_HIS_ANALYTICS;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAuthority('VENDOR')")
public class VendorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendorController.class);

    private final VendorService vendorService;
    private final JwtUtil jwtUtil;

    @Autowired
    public VendorController(VendorService vendorService, JwtUtil jwtUtil) {
        this.vendorService = vendorService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * This controller is for handling the API calls to view all items on sale
     *
     * @return Response Entity with status code 201 and displays details of all items available on sale in the body
     */
    @GetMapping(value = "/view/items", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view all items on sale")
    public ResponseEntity<ResponseMessageWithPagination> viewAllItemOnSale(@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
        return vendorService.viewAllItemOnSale(pageNo, pageSize);
    }

    /**
     * This controller is for handling the API calls to purchase items available on sale from collector
     *
     * @param acceptItemOnSaleDTO which contains fields (quantity and price)
     * @param request               HTTPServletRequest
     * @return Response Entity with status code 201 and displays details of item purchased in the body
     */

    @PostMapping(value = "/items/accept", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to purchase items available on sale from collector")
    public ResponseEntity<ResponseMessage> purchaseItemsOnSale(@RequestBody AcceptItemOnSaleDTO acceptItemOnSaleDTO, HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching his profile details", id);
        return vendorService.purchaseItemsOnSale(acceptItemOnSaleDTO, request);
    }

    /**
     * This controller is for handling the API calls to view all past purchased order details
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 201 and displays details of past purchased orders in the body
     */
    @GetMapping(value = "/purchase/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view all past purchased order details")
    public ResponseEntity<ResponseMessage> orderSummary(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug("User with id '{}' is fetching his profile details", id);
        return vendorService.orderSummary(request);
    }

    /**
     * This controller is for handling the API calls to view collector profile in purchased order summary
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 201 and displays collector details in purchased orders summary in the body
     */

    @GetMapping(value = "/purchase-summary/collector-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view collector profile in purchased order summary")
    public ResponseEntity<ResponseMessage> viewCollectorProfileInSummary(@RequestParam(name = "user_id") Long id, HttpServletRequest request) {
        return vendorService.viewCollectorProfileInSummary(id, request);
    }

    /**
     * This controller is for handling the API calls to view collector profile in items available on sale
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 201 and displays collector details for items available on sale in the body
     */

    @GetMapping(value = "view/items/collector-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view collector profile in items available on sale")
    public ResponseEntity<ResponseMessage> viewCollectorProfile(@RequestParam(name = "user_id") Long id, HttpServletRequest request) {
        return vendorService.viewCollectorProfile(id, request);
    }

    /**
     * This controller is for handling the API calls to view Analytics(Collector in city vs Total Number of collector)
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/vendor/analytics/view-collector", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This controller is for handling the API calls to view Analytics(Collector in city vs Total Number of collector")
    public ResponseEntity<ResponseMessage> getAnalyticsV1(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return vendorService.getAnalytics(request);
    }

    /**
     * This controller is for handling the API calls to view Analytics
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/vendor/analytics/v2", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseMessage> getAnalyticsV2(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return vendorService.getAnalyticsV2(request);
    }

    /**
     * This controller is for handling the API calls to view Analytics
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     */

    @GetMapping(value = "/vendor/analytics/v4", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseMessage> getAnalyticsV4(HttpServletRequest request) {
        String id = jwtUtil.fetchId(request.getHeader(AUTHORIZATION).substring(7));
        LOGGER.debug(USER_WITH_ID_VIEWED_HIS_ANALYTICS, id);
        return vendorService.getAnalyticsV4(request);
    }

}
