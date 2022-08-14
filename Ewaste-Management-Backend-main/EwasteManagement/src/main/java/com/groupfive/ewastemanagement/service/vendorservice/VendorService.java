package com.groupfive.ewastemanagement.service.vendorservice;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.model.AcceptSellItemsVendor;
import com.groupfive.ewastemanagement.model.UserModel;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface VendorService {

    ResponseEntity<EnvelopeMessage> viewProfile(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewAccept(AcceptSellItemsVendor acceptSellItemsVendor, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> editProfile(UserModel userModel, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewAllItemOnSale();

    ResponseEntity<EnvelopeMessage> orderSummary(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewCollectorProfileInSummary(Long id, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalytics(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalyticsV2(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalyticsV4(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewCollectorProfile(Long id, HttpServletRequest request);
}
