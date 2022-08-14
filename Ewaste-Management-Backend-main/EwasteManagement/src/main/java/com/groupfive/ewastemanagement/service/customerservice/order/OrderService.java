package com.groupfive.ewastemanagement.service.customerservice.order;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.model.RequestModel;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface OrderService {
    ResponseEntity<EnvelopeMessage> createPickUpRequest(RequestModel order, HttpServletRequest request);

    int showDropOffLocation(String category,HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> viewListOfCollectorsForDropOff(String category, HttpServletRequest request);

    int countCollectorPickUp(String category, HttpServletRequest request);

    ResponseEntity<EnvelopeMessage> createDropOffRequest(RequestModel ordersModel, HttpServletRequest request);
}
