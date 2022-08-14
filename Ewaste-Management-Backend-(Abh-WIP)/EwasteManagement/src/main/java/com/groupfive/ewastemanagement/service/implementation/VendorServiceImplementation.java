package com.groupfive.ewastemanagement.service.implementation;

import com.groupfive.ewastemanagement.dto.request.AcceptItemOnSaleDTO;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.response.ResponseMessageWithPagination;
import com.groupfive.ewastemanagement.entity.*;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.exception.NoDataException;
import com.groupfive.ewastemanagement.exception.NotFoundException;
import com.groupfive.ewastemanagement.repository.*;
import com.groupfive.ewastemanagement.service.VendorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.groupfive.ewastemanagement.helper.Constants.*;

@Service
public class VendorServiceImplementation implements VendorService {
    private final SellItemRepo sellItemRepo;
    private final UserRepo userRepo;
    private final VendorOrdersRepo vendorOrdersRepo;
    private final UserDetailsRepo userDetailsRepo;
    private final NotificationRepo notificationRepo;
    private final CategoriesAcceptedRepo categoriesAcceptedRepo;
    private static final Logger LOGGER = LoggerFactory.getLogger(VendorServiceImplementation.class);

    @Autowired
    public VendorServiceImplementation(SellItemRepo sellItemRepo, UserRepo userRepo, VendorOrdersRepo vendorOrdersRepo, UserDetailsRepo userDetailsRepo, NotificationRepo notificationRepo, CategoriesAcceptedRepo categoriesAcceptedRepo) {
        this.sellItemRepo = sellItemRepo;
        this.userRepo = userRepo;
        this.vendorOrdersRepo = vendorOrdersRepo;
        this.userDetailsRepo = userDetailsRepo;
        this.notificationRepo = notificationRepo;
        this.categoriesAcceptedRepo = categoriesAcceptedRepo;
    }

    /**
     * This function of service is for purchasing items available on sale
     *
     * @param acceptItemOnSaleDTO which contains id, quantity,price and date
     * @param request               HTTPServletRequest
     * @return Response Entity with status code 200 and display details of purchased item available on sale in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> purchaseItemsOnSale(AcceptItemOnSaleDTO acceptItemOnSaleDTO, HttpServletRequest request) {
        LOGGER.info("Purchase Items On Sale :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);

        if (email == null) {
            LOGGER.error(EMPTY_EMAIL);
            throw new BadRequestException(EMAIL_CANNOT_BE_EMPTY);
        }

        User user;
        try {
            user = userRepo.findUserByEmail(email);

            Long id = acceptItemOnSaleDTO.getId();
            int quantity = Integer.parseInt(acceptItemOnSaleDTO.getQuantity());

            SellItems sellItems = sellItemRepo.findByIdAndStatus(id, AVAILABLE);

            ResponseMessage responseMessage = new ResponseMessage();

            VendorOrders vendorOrders = new VendorOrders();
            int quantityCollector = Integer.parseInt(sellItems.getAvailableQuantity());

            if (quantity > quantityCollector) {
                responseMessage.setStatus(FAIL);
                responseMessage.setData(REDUCE_QUANTITY);
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
            if (quantityCollector == 0) {
                sellItems.setStatus(SOLD);
                sellItemRepo.save(sellItems);

                responseMessage.setStatus(FAIL);
                responseMessage.setData(sellItems);

                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            } else {
                updateStatusForPurchaseItems(quantityCollector, quantity, sellItems, vendorOrders, acceptItemOnSaleDTO, user);
            }
            sellItemRepo.save(sellItems);

            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(vendorOrders);

            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
    }


    /**
     * This function of service is for viewing the list of items available on sale
     *
     * @return Response Entity with status code 200 and display list of items available on sale in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessageWithPagination> viewAllItemOnSale(int pageNo, int pageSize) {
        LOGGER.info(VIEW_ITEMS_ON_SALE + API_HAS_STARTED_SUCCESSFULLY);

        Page<SellItems> sellItemsPage = sellItemRepo.findAllByStatus(AVAILABLE, PageRequest.of(pageNo - 1, pageSize));
        List<SellItems> sellItems = sellItemsPage.getContent();

        for (SellItems items : sellItems) {
            if (items.getAvailableQuantity().equals("0")) {
                LOGGER.error("Item " + OUT_OF_STOCK);
                items.setStatus(OUT_OF_STOCK);

                LOGGER.info(VIEW_ITEMS_ON_SALE + "status updated");
                sellItemRepo.save(items);
            }
        }
        sellItems = sellItemRepo.findAllByStatus(AVAILABLE, PageRequest.of(pageNo - 1, pageSize)).getContent();

        ResponseMessageWithPagination responseMessage = new ResponseMessageWithPagination();
        if (sellItems.isEmpty()) {
            LOGGER.error(VIEW_ITEMS_ON_SALE + "Failed due to " + NO_ITEM_FOUND);

            responseMessage.setStatus(FAIL);
            responseMessage.setPageNo(pageNo);
            responseMessage.setPageSize(pageSize);
            responseMessage.setTotalRecords(sellItems.size());

            responseMessage.setData(NO_ITEM_FOUND);
            throw new NoDataException(NO_ITEM_FOUND + " on sale");
        }
        responseMessage.setStatus(SUCCESS);
        responseMessage.setPageNo(pageNo);
        responseMessage.setPageSize(pageSize);
        responseMessage.setTotalRecords(sellItems.size());
        LOGGER.info(VIEW_ITEMS_ON_SALE + " fetched successfully");
        responseMessage.setData(sellItems);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for viewing the vendor purchased item summary
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and display details of purchased items summary in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> orderSummary(HttpServletRequest request) {
        String email = request.getHeader(EMAIL);

        User user = userRepo.findUserByEmail(email);
        List<VendorOrders> vendorOrdersList = vendorOrdersRepo.findAllByVendorUid(user.getUid());

        ResponseMessage responseMessage = new ResponseMessage();
        if (vendorOrdersList.isEmpty()) {
            responseMessage.setStatus(FAIL);
            responseMessage.setData(NO_ORDERS);

            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(vendorOrdersList);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for viewing the collector profile in sold sales summary
     *
     * @param id      Long Parameter
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and display details of collector in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */
    @Override
    public ResponseEntity<ResponseMessage> viewCollectorProfileInSummary(Long id, HttpServletRequest request) {
        if (id == null) {
            throw new InvalidUserException(NO_COLLECTOR_FOUND);
        } else {
            VendorOrders vendorOrders;

            vendorOrders = vendorOrdersRepo.findById(id).orElseThrow(NotFoundException::new);

            User collector = userRepo.findUserByUid(vendorOrders.getSellItems().getCollectorUid());
            ResponseMessage responseMessage = new ResponseMessage();
            if (collector == null) {
                responseMessage.setStatus(FAIL);
                responseMessage.setData(NO_COLLECTOR_FOUND);

                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(collector);

            return new ResponseEntity<>(responseMessage, HttpStatus.OK);

        }
    }

    /**
     * This function of service is for viewing the collector profile in available sales summary
     *
     * @param id      Long Parameter
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and display details of collector in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */
    @Override
    public ResponseEntity<ResponseMessage> viewCollectorProfile(Long id, HttpServletRequest request) {
        if (id == null) {
            throw new InvalidUserException(NO_COLLECTOR_FOUND);
        } else {
            SellItems sellItemsList = sellItemRepo.findById(id).orElseThrow(NotFoundException::new);

            User collector;
            collector = userRepo.findUserByUid(sellItemsList.getCollectorUid());

            ResponseMessage responseMessage = new ResponseMessage();
            if (collector == null) {
                responseMessage.setStatus(FAIL);
                responseMessage.setData(NO_COLLECTOR_FOUND);

                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(collector);

            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
    }

    /**
     * This function of service is for analytics
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> getAnalytics(HttpServletRequest request) {
        LOGGER.info("Get Analytics :: " + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);
        if (email == null) {
            LOGGER.error(EMPTY_EMAIL);
            throw new BadRequestException(EMAIL_CANNOT_BE_EMPTY);
        }

        User vendor;
        try {
            vendor = userRepo.findUserByEmail(email);

            HashMap<String, Integer> hashMap = new HashMap<>();
            List<User> vendorList = userRepo.findAllUsersByRoleAndCity(VENDOR, vendor.getCity());
            List<User> allVendorList = userRepo.findAllUsersByRole(VENDOR);

            hashMap.put(VENDOR_IN_CITY, vendorList.size());
            hashMap.put(ALL_VENDOR, allVendorList.size());

            List<User> collectorList = userRepo.findAllUsersByRoleAndCity(COLLECTOR, vendor.getCity());
            List<User> allCollectorList = userRepo.findAllUsersByRole(COLLECTOR);

            hashMap.put(COLLECTOR_IN_CITY, collectorList.size());
            hashMap.put(ALL_COLLECTOR, allCollectorList.size());

            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(hashMap);

            LOGGER.info(ANALYTICS + FETCHED_SUCCESSFULLY);

            return new ResponseEntity<>(responseMessage, HttpStatus.OK);

        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
    }

    /**
     * This function of service is for analytics
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> getAnalyticsV2(HttpServletRequest request) {
        LOGGER.info(GET_ANALYTICS + API_HAS_STARTED_SUCCESSFULLY);

        String email = request.getHeader(EMAIL);
        User vendor = userRepo.findUserByEmail(email);

        HashMap<String, Integer> hashMap = new HashMap<>();

        hashMap.put(TEMP_COLLECTOR_SALE, 0);
        hashMap.put(LAMPS_COLLECTOR_SALE, 0);
        hashMap.put(LARGE_EQUIP_COLLECTOR_SALE, 0);
        hashMap.put(SMALL_EQUIP_COLLECTOR_SALE, 0);
        hashMap.put(SMALL_IT_COLLECTOR_SALE, 0);
        hashMap.put(SCREENS_COLLECTOR_SALE, 0);

        hashMap.put(TEMP_VENDOR, 0);
        hashMap.put(LAMPS_VENDOR, 0);
        hashMap.put(LARGE_EQUIP_VENDOR, 0);
        hashMap.put(SMALL_EQUIP_VENDOR, 0);
        hashMap.put(SMALL_IT_VENDOR, 0);
        hashMap.put(SCREENS_VENDOR, 0);

        List<User> collectorList = userRepo.findAllUsersByRoleAndCity(COLLECTOR, vendor.getCity());

        for (User collector : collectorList) {
            List<SellItems> sellItemsListTemp = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(TEMP, collector.getUid());
            hashMap.put(TEMP_COLLECTOR_SALE, hashMap.get(TEMP_COLLECTOR_SALE) + sellItemsListTemp.size());

            List<SellItems> sellItemsListLamp = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(LAMPS, collector.getUid());
            hashMap.put(LAMPS_COLLECTOR_SALE, hashMap.get(LAMPS_COLLECTOR_SALE) + sellItemsListLamp.size());

            List<SellItems> sellItemsListLargeEquip = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(LARGE_EQUIP, collector.getUid());
            hashMap.put(LARGE_EQUIP_COLLECTOR_SALE, hashMap.get(LARGE_EQUIP_COLLECTOR_SALE) + sellItemsListLargeEquip.size());

            List<SellItems> sellItemsListSmallEquip = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(SMALL_EQUIP, collector.getUid());
            hashMap.put(SMALL_EQUIP_COLLECTOR_SALE, hashMap.get(SMALL_EQUIP_COLLECTOR_SALE) + sellItemsListSmallEquip.size());

            List<SellItems> sellItemsListSmallIT = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(SMALL_IT, collector.getUid());
            hashMap.put(SMALL_IT_COLLECTOR_SALE, hashMap.get(SMALL_IT_COLLECTOR_SALE) + sellItemsListSmallIT.size());

            List<SellItems> sellItemsListScreens = sellItemRepo.findByCategoryCategoryAcceptedAndCollectorUid(SCREENS, collector.getUid());
            hashMap.put(SCREENS_COLLECTOR_SALE, hashMap.get(SCREENS_COLLECTOR_SALE) + sellItemsListScreens.size());

        }

        for (User collector : collectorList) {
            List<VendorOrders> vendorOrdersList1 = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(TEMP, collector.getUid());
            hashMap.put(TEMP_VENDOR, hashMap.get(TEMP_VENDOR) + vendorOrdersList1.size());

            List<VendorOrders> vendorOrdersList2 = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(LAMPS, collector.getUid());
            hashMap.put(LAMPS_VENDOR, hashMap.get(LAMPS_VENDOR) + vendorOrdersList2.size());

            List<VendorOrders> vendorOrdersList3 = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(LARGE_EQUIP, collector.getUid());
            hashMap.put(LARGE_EQUIP_VENDOR, hashMap.get(LARGE_EQUIP_VENDOR) + vendorOrdersList3.size());

            List<VendorOrders> vendorOrdersList4 = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(SMALL_EQUIP, collector.getUid());
            hashMap.put(SMALL_EQUIP_VENDOR, hashMap.get(SMALL_EQUIP_VENDOR) + vendorOrdersList4.size());

            List<VendorOrders> vendorOrdersList5 = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(SMALL_IT, collector.getUid());
            hashMap.put(SMALL_IT_VENDOR, hashMap.get(SMALL_IT_VENDOR) + vendorOrdersList5.size());

            List<VendorOrders> vendorOrdersList6 = vendorOrdersRepo.findOrdersByCollectorUidAndCategory(SCREENS, collector.getUid());
            hashMap.put(SCREENS_VENDOR, hashMap.get(SCREENS_VENDOR) + vendorOrdersList6.size());
        }

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(hashMap);

        LOGGER.info(ANALYTICS + FETCHED_SUCCESSFULLY);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);

    }

    /**
     * This function of service is for analytics
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays analytics in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> getAnalyticsV4(HttpServletRequest request) {
        LOGGER.info(GET_ANALYTICS + API_HAS_STARTED_SUCCESSFULLY);
        String email = request.getHeader(EMAIL);
        if (email == null) {
            LOGGER.error(EMPTY_EMAIL);
            throw new BadRequestException(EMAIL_CANNOT_BE_EMPTY);
        }
        User vendor;
        try {
            vendor = userRepo.findUserByEmail(email);

            List<User> collectorList = userRepo.findAllUsersByRoleAndCity(COLLECTOR, vendor.getCity());

            HashMap<String, Integer> hashMap = new HashMap<>();
            hashMap.put(TEMP_CITY, 0);
            hashMap.put(LAMPS_CITY, 0);
            hashMap.put(LARGE_EQUIP_CITY, 0);
            hashMap.put(SMALL_EQUIP_CITY, 0);
            hashMap.put(SMALL_IT_CITY, 0);
            hashMap.put(SCREENS_CITY, 0);

            List<UserDetails> collectorDetailsList = new ArrayList<>();
            for (User user : collectorList) {
                collectorDetailsList.add(userDetailsRepo.findUserByUid(user.getUid()));
            }

            for (UserDetails collector : collectorDetailsList) {
                List<CategoriesAccepted> list1 = categoriesAcceptedRepo.findAllByCategoryAccepted(TEMP, collector.getId());
                hashMap.put(TEMP_CITY, hashMap.get(TEMP_CITY) + list1.size());

                List<CategoriesAccepted> list2 = categoriesAcceptedRepo.findAllByCategoryAccepted(LAMPS, collector.getId());
                hashMap.put(LAMPS_CITY, hashMap.get(LAMPS_CITY) + list2.size());

                List<CategoriesAccepted> list3 = categoriesAcceptedRepo.findAllByCategoryAccepted(LARGE_EQUIP, collector.getId());
                hashMap.put(LARGE_EQUIP_CITY, hashMap.get(LARGE_EQUIP_CITY) + list3.size());

                List<CategoriesAccepted> list4 = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_EQUIP, collector.getId());
                hashMap.put(SMALL_EQUIP_CITY, hashMap.get(SMALL_EQUIP_CITY) + list4.size());

                List<CategoriesAccepted> list5 = categoriesAcceptedRepo.findAllByCategoryAccepted(SMALL_IT, collector.getId());
                hashMap.put(SMALL_IT_CITY, hashMap.get(SMALL_IT_CITY) + list5.size());

                List<CategoriesAccepted> list6 = categoriesAcceptedRepo.findAllByCategoryAccepted(SCREENS, collector.getId());
                hashMap.put(SCREENS_CITY, hashMap.get(SCREENS_CITY) + list6.size());
            }
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(hashMap);

            LOGGER.info(ANALYTICS + FETCHED_SUCCESSFULLY);

            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            throw new InvalidUserException(USER_DOES_NOT_EXIST);
        }
    }


    public void updateStatusForPurchaseItems(int quantityCollector, int quantity, SellItems sellItems, VendorOrders vendorOrders, AcceptItemOnSaleDTO acceptItemOnSaleDTO, User user) {
        quantityCollector = quantityCollector - quantity;

        sellItems.setAvailableQuantity(String.valueOf(quantityCollector));

        vendorOrders.setSellItems(sellItems);

        vendorOrders.setVendorUid(user.getUid());

        vendorOrders.setDate(acceptItemOnSaleDTO.getDate());

        User collector = userDetailsRepo.findUserByUid(sellItems.getCollectorUid()).getUser();
        String address = collector.getAddress1() + " " + collector.getCity() + " " + collector.getState();

        vendorOrders.setAddress(address);
        vendorOrders.setQuantity(String.valueOf(quantity));
        vendorOrders.setStatus(COMPLETED);
        vendorOrders.setPrice(acceptItemOnSaleDTO.getPrice());

        Notification vendorNotification = new Notification();
        vendorNotification.setCollectorUid(sellItems.getCollectorUid());
        vendorNotification.setRole("Collector");
        vendorNotification.setStatus(false);
        vendorNotification.setMessage("Your " + vendorOrders.getSellItems().getItemName() + " is purchased by the vendor");

        notificationRepo.save(vendorNotification);
        LOGGER.info("Purchase Items On Sale :: " + "Item purchased successfully");

        vendorOrdersRepo.save(vendorOrders);
    }


}