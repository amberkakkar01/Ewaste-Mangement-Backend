package com.groupfive.ewastemanagement.service.customerservice;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.model.UserModel;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CustomerService {

    List<String> getCollectorUidBasedOnCity(String city, String category);

    ResponseEntity<EnvelopeMessage> getAllOrders(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewProfile(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> editProfile(UserModel userModel, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewDrives(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewNotification(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewCollectorProfile(String uid);

    ResponseEntity<EnvelopeMessage> readNotification(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalytics(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalyticsV2(HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> getAnalyticsV3(HttpServletRequest request);
}
