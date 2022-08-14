package com.groupfive.ewastemanagement.service;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CustomerService {

    List<String> getCollectorBasedOnCity(String city, String category);

    ResponseEntity<ResponseMessageWithPagination> getAllOrders(int pageNo, int pageSize, HttpServletRequest request);

    ResponseEntity<ResponseMessage> viewDrives(HttpServletRequest request);

    ResponseEntity<ResponseMessage> viewNotification(HttpServletRequest request);

    ResponseEntity<ResponseMessage> viewCollectorProfile(String uid);

    ResponseEntity<ResponseMessage> readNotification(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalytics(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalyticsV2(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalyticsForCollectorInCity(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalyticsForAllCollector(HttpServletRequest request);

}
