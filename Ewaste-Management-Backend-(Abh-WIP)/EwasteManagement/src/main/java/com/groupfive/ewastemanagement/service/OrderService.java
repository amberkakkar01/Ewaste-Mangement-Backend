package com.groupfive.ewastemanagement.service;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.request.RequestDTO;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;


public interface OrderService {
    ResponseEntity<ResponseMessage> createPickUpRequest(RequestDTO order, HttpServletRequest request);

    int showDropOffLocation(String category, HttpServletRequest request);

    ResponseEntity<ResponseMessage> viewListOfCollectorsForDropOff(String category, HttpServletRequest request);

    int countCollectorPickUp(String category, HttpServletRequest request);

    ResponseEntity<ResponseMessage> createDropOffRequest(RequestDTO ordersModel, HttpServletRequest request);
}
