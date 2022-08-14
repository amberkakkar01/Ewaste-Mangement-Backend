package com.groupfive.ewastemanagement.controller;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.model.EWasteDriveModel;
import com.groupfive.ewastemanagement.model.SellItemModel;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.service.collectorservice.CollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/collector")
@CrossOrigin
public class CollectorController {

    @Autowired
    CollectorService collectorService;

    @GetMapping("/request/pending")
    public ResponseEntity<EnvelopeMessage>getPendingRequest(HttpServletRequest request)//@RequestBody PendingRequest pendingRequest)
    {
        return collectorService.getPendingRequest(request);
    }

    @GetMapping("/request/summary")
    public ResponseEntity<EnvelopeMessage>getRequestSummary(HttpServletRequest request)//@RequestBody PendingRequest pendingRequest)
    {
        return collectorService.getRequestSummary(request);
    }

    @PostMapping("/request/pending/accept")
    public ResponseEntity<EnvelopeMessage>acceptPendingRequest(@RequestParam(name = "order") String orderId, HttpServletRequest request)
    {
        return collectorService.acceptPendingRequest(orderId,request);
    }

    @PostMapping("/drive/organize")
    public ResponseEntity<EnvelopeMessage>organizeDrive(@Valid @RequestBody EWasteDriveModel eWasteDriveModel, HttpServletRequest request)
    {
        return collectorService.organizeDrive(eWasteDriveModel,request);
    }

    @GetMapping("/drive/myDrive")
    public ResponseEntity<EnvelopeMessage>viewMyDrive(HttpServletRequest request)
    {
        return collectorService.viewMyDrive(request);
    }

    @PostMapping("/sell")
    public ResponseEntity<EnvelopeMessage>sellItem(@Valid @RequestBody SellItemModel sellItemModel, HttpServletRequest request)
    {
        return collectorService.sellItem(sellItemModel,request);
    }

    @GetMapping("/sell/summary/available")
    public ResponseEntity<EnvelopeMessage>sellItemAvailableSummary(HttpServletRequest request)
    {
        return collectorService.sellItemAvailableSummary(request);
    }

    @GetMapping("/sell/summary/sold")
    public ResponseEntity<EnvelopeMessage>sellItemSoldSummary(HttpServletRequest request)
    {
        return collectorService.sellItemSoldSummary(request);
    }

    @PutMapping("/sell/summary/available/edit")
    public ResponseEntity<EnvelopeMessage>editSellItemAvailableSummary(HttpServletRequest request,@Valid @RequestBody SellItemModel sellItemModel)
    {
        return collectorService.editSellItemAvailableSummary(request,sellItemModel);
    }

    @GetMapping("/profile/view")
    public ResponseEntity<EnvelopeMessage>viewProfile(HttpServletRequest request)
    {
        return collectorService.viewProfile(request);
    }

    @PutMapping("/profile/edit")
    public ResponseEntity<EnvelopeMessage>editProfile(@RequestBody UserModel userModel , HttpServletRequest request)
    {
        return collectorService.editProfile(userModel,request);
    }

    @GetMapping("/notification")
    public ResponseEntity<EnvelopeMessage>viewNotification(HttpServletRequest request)
    {
        return collectorService.viewNotification(request);
    }

    @PostMapping("/notification/markAsRead")
    public ResponseEntity<EnvelopeMessage>readNotification(HttpServletRequest request)
    {
        return collectorService.readNotification(request);
    }

    // Status in drive
    @PutMapping("drive/myDrive/edit")
    public ResponseEntity<EnvelopeMessage>editDriveSummary(@RequestParam(name = "id") Long id,@RequestParam(name = "status") String status, HttpServletRequest request)
    {
        return collectorService.editDriveSummary(id,status,request);
    }

    @GetMapping("/request/pending/customerProfile")
    public ResponseEntity<EnvelopeMessage>viewCustomerProfile(@RequestParam(name = "id") String uid)
    {
        return collectorService.viewCustomerProfile(uid);
    }

    //E-waste drives you organized v/s overall E-waste drives in the city
    @GetMapping("/analytics/v1")
    public ResponseEntity<EnvelopeMessage> getAnalyticsV1(HttpServletRequest request)
    {
        return collectorService.getAnalytics(request);
    }

    //Items that are collected v/s Items that are sold
    @GetMapping("/analytics/v2")
    public ResponseEntity<EnvelopeMessage> getAnalyticsV2(HttpServletRequest request)
    {
        return collectorService.getAnalyticsV2(request);
    }

    //No of Vendor in city Vs Total No of Vendor
    @GetMapping("/analytics/v4")
    public ResponseEntity<EnvelopeMessage> getAnalyticsV4(HttpServletRequest request)
    {
        return collectorService.getAnalyticsV4(request);
    }

    //No of Customer in city Vs Total No of Customer
    @GetMapping("/analytics/v5")
    public ResponseEntity<EnvelopeMessage> getAnalyticsV5(HttpServletRequest request)
    {
        return collectorService.getAnalyticsV5(request);
    }

    @GetMapping("/analytics/v6")
    public ResponseEntity<EnvelopeMessage>getAnalyticsV6(HttpServletRequest request)
    {
        return collectorService.getAnalyticsV6(request);
    }
}
