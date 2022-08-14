package com.groupfive.ewastemanagement.controller;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.model.AcceptSellItemsVendor;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.service.vendorservice.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/vendor")
@CrossOrigin
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @GetMapping("/view/items")
    public ResponseEntity<EnvelopeMessage> viewAllItemOnSale()
    {
        return vendorService.viewAllItemOnSale();
    }

    @PostMapping("/view/items/accept")
    public ResponseEntity<EnvelopeMessage> viewAccept(@RequestBody AcceptSellItemsVendor acceptSellItemsVendor, HttpServletRequest request)
    {
        return vendorService.viewAccept(acceptSellItemsVendor,request);
    }

    @GetMapping("/profile/view")
    public ResponseEntity<EnvelopeMessage>viewProfile(HttpServletRequest request)
    {
        return vendorService.viewProfile(request);
    }

    @PutMapping("/profile/edit")
    public ResponseEntity<EnvelopeMessage>editProfile(@Valid @RequestBody UserModel userModel , HttpServletRequest request)
    {
        return vendorService.editProfile(userModel,request);
    }

    @GetMapping("/summary")
    public ResponseEntity<EnvelopeMessage>orderSummary(HttpServletRequest request)
    {
        return vendorService.orderSummary(request);
    }

    @GetMapping("/view/items/accept/collectorProfile")
    public ResponseEntity<EnvelopeMessage> viewCollectorProfileInSummary(@RequestParam(name = "id") Long id, HttpServletRequest request)
    {
        return vendorService.viewCollectorProfileInSummary(id,request);
    }

    @GetMapping("view/items/collectorProfile")
    public ResponseEntity<EnvelopeMessage> viewCollectorProfile(@RequestParam(name = "id") Long id, HttpServletRequest request)
    {
        return vendorService.viewCollectorProfile(id,request);
    }

    //Collector in City All Vs  Total collector
    @GetMapping("/analytics/v1")
    public ResponseEntity<EnvelopeMessage>getAnalyticsV1(HttpServletRequest request)
    {
        return vendorService.getAnalytics(request);
    }

    @GetMapping("/analytics/v2")
    public ResponseEntity<EnvelopeMessage>getAnalyticsV2(HttpServletRequest request)
    {
        return vendorService.getAnalyticsV2(request);
    }

    @GetMapping("/analytic/v4")
    public ResponseEntity<EnvelopeMessage>getAnalyticsV4(HttpServletRequest request)
    {
        return vendorService.getAnalyticsV4(request);
    }

}
