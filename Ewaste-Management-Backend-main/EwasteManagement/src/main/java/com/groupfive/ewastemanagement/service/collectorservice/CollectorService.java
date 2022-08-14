package com.groupfive.ewastemanagement.service.collectorservice;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.model.EWasteDriveModel;
import com.groupfive.ewastemanagement.model.SellItemModel;
import com.groupfive.ewastemanagement.model.UserModel;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface CollectorService {
    ResponseEntity<EnvelopeMessage> getPendingRequest(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getRequestSummary(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> acceptPendingRequest(String orderId, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> organizeDrive(EWasteDriveModel eWasteDriveModel,HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> sellItem(SellItemModel sellItemModel, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewMyDrive(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewProfile(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> editProfile(UserModel userModel, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewNotification(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> sellItemAvailableSummary(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> sellItemSoldSummary(HttpServletRequest request);
    
    ResponseEntity<EnvelopeMessage> editDriveSummary(Long id, String status, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewCustomerProfile(String uid);

    ResponseEntity<EnvelopeMessage> getAnalytics(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalyticsV2(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> readNotification(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalyticsV4(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalyticsV5(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalyticsV6(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> editSellItemAvailableSummary(HttpServletRequest request, SellItemModel sellItemModel);
}
