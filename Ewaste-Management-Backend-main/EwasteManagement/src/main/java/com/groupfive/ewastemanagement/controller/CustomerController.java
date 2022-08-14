package com.groupfive.ewastemanagement.controller;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.model.RequestModel;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.service.customerservice.CustomerService;
import com.groupfive.ewastemanagement.service.customerservice.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/customer")
@CrossOrigin
public class CustomerController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/request/pickUp")
    public ResponseEntity<EnvelopeMessage> createPickUpRequest(@Valid @RequestBody RequestModel ordersModel, HttpServletRequest request)
    {
        return orderService.createPickUpRequest(ordersModel,request);
    }

    @PostMapping("/request/dropOff")
    public ResponseEntity<EnvelopeMessage> createDropOffRequest(@Valid @RequestBody RequestModel ordersModel, HttpServletRequest request)
    {
        return orderService.createDropOffRequest(ordersModel,request);
    }

    // count of drop off Location
    @GetMapping("/request/dropOff/details")
    public int searchDropOffLocation(@RequestParam(name = "category") String category,HttpServletRequest request) {
        return orderService.showDropOffLocation(category,request);
    }

    //shows the list of collector accepting Drop-Off
    @GetMapping("/request/dropOff/viewCollectors")
    public ResponseEntity<EnvelopeMessage> viewListOfCollectorsForDropOff(@RequestParam(name = "category") String category,HttpServletRequest request) {
        return orderService.viewListOfCollectorsForDropOff(category,request);
    }

    // count of view collectors
    @GetMapping("/request/pickUp/viewCollectors")
    public int countCollectorPickUp(@RequestParam(name = "category") String category, HttpServletRequest request)
    {
        return orderService.countCollectorPickUp(category,request);
    }
    @GetMapping("/profile/view")
    public ResponseEntity<EnvelopeMessage>viewProfile(HttpServletRequest request)
    {
        return customerService.viewProfile(request);
    }

    //Request Summary:-> Change maping to summary
    @GetMapping("/request/all")
    public ResponseEntity<EnvelopeMessage>getAllOrders(HttpServletRequest request)
    {
        return customerService.getAllOrders(request);
    }

    @PutMapping("/profile/edit")
    public ResponseEntity<EnvelopeMessage>editProfile(@Valid @RequestBody UserModel userModel , HttpServletRequest request)
    {
        return customerService.editProfile(userModel,request);
    }

    @GetMapping("/viewDrives")
    public ResponseEntity<EnvelopeMessage> viewEWasteDrives(HttpServletRequest request)
    {
        return customerService.viewDrives(request);
    }

    @GetMapping("/notification")
    public ResponseEntity<EnvelopeMessage>viewNotification(HttpServletRequest request)
    {
        return customerService.viewNotification(request);
    }

    @PostMapping("/notification/markAsRead")
    public ResponseEntity<EnvelopeMessage>readNotification(HttpServletRequest request)
    {
        return customerService.readNotification(request);
    }

    @GetMapping("/request/all/collectorProfile")
    public ResponseEntity<EnvelopeMessage>viewCollectorProfile(@RequestParam(name = "id") String uid){
        return customerService.viewCollectorProfile(uid);
    }

    //No of E-Waste Drives in your City v/s Total number of E-Waste Drives
    @GetMapping("/analytics/v1")
    public ResponseEntity<EnvelopeMessage>getAnalyticsV1(HttpServletRequest request)
    {
        return customerService.getAnalytics(request);
    }

    //E-waste generated in your city v/s e-waste you donated
    @GetMapping("/analytics/v2")
    public ResponseEntity<EnvelopeMessage>getAnalyticsV2(HttpServletRequest request)
    {
        return customerService.getAnalyticsV2(request);
    }

    //Collector’s in your city with categories they accept VS Total No of Collectors
    @GetMapping("/analytics/v3")
    public ResponseEntity<EnvelopeMessage>getAnalyticsV3(HttpServletRequest request)
    {
        return customerService.getAnalyticsV3(request);
    }
}
