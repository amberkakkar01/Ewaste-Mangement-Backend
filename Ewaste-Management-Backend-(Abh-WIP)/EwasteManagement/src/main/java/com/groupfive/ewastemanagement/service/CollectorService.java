package com.groupfive.ewastemanagement.service;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.AllPendingRequest;
import com.groupfive.ewastemanagement.dto.request.EWasteDriveDTO;
import com.groupfive.ewastemanagement.dto.request.SellItemDTO;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;


public interface CollectorService {
    ResponseEntity<ResponseMessageWithPagination> getPendingRequest(int pageNo, int pageSize, HttpServletRequest request);

    ResponseEntity<ResponseMessageWithPagination> getRequestSummary(int pageNo, int pageSize, HttpServletRequest request);

    ResponseEntity<ResponseMessage> acceptPendingRequest(String orderId, HttpServletRequest request);

    ResponseEntity<ResponseMessage> organizeDrive(EWasteDriveDTO eWasteDriveDTO, HttpServletRequest request);

    ResponseEntity<ResponseMessage> sellItem(SellItemDTO sellItemDTO, HttpServletRequest request);

    ResponseEntity<ResponseMessageWithPagination> viewMyDrive(int pageNo, int pageSize, HttpServletRequest request);

    ResponseEntity<ResponseMessage> viewNotification(HttpServletRequest request);

    ResponseEntity<ResponseMessage> sellItemAvailableSummary(HttpServletRequest request);

    ResponseEntity<ResponseMessage> sellItemSoldSummary(HttpServletRequest request);

    ResponseEntity<ResponseMessage> editDriveSummary(Long id, String status, HttpServletRequest request);

    ResponseEntity<ResponseMessage> viewCustomerProfile(String uid);

    ResponseEntity<ResponseMessage> getAnalytics(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalyticsV2(HttpServletRequest request);

    ResponseEntity<ResponseMessage> readNotification(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalyticsV4(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalyticsV5(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalyticsV6(HttpServletRequest request);

    void expirePendingRequest(AllPendingRequest allPendingRequest);

    ResponseEntity<ResponseMessage> editSellItemAvailableSummary(HttpServletRequest request, SellItemDTO sellItemDTO);

}