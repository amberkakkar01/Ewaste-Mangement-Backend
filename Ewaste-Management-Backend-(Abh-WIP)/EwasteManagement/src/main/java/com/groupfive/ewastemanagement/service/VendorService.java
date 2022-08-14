package com.groupfive.ewastemanagement.service;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.dto.request.AcceptItemOnSaleDTO;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface VendorService {


    ResponseEntity<ResponseMessage> purchaseItemsOnSale(AcceptItemOnSaleDTO acceptItemOnSaleDTO, HttpServletRequest request);

    ResponseEntity<ResponseMessageWithPagination> viewAllItemOnSale(int pageNo, int pageSize);

    ResponseEntity<ResponseMessage> orderSummary(HttpServletRequest request);

    ResponseEntity<ResponseMessage> viewCollectorProfileInSummary(Long id, HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalytics(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalyticsV2(HttpServletRequest request);

    ResponseEntity<ResponseMessage> getAnalyticsV4(HttpServletRequest request);

    ResponseEntity<ResponseMessage> viewCollectorProfile(Long id, HttpServletRequest request);
}
