package com.groupfive.ewastemanagement.repository;

import com.groupfive.ewastemanagement.entity.Orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepo extends JpaRepository<Orders,Long> {

    Page<Orders> findAllByCustomerUid(String uid, Pageable pageable);

    List<Orders> findAllAnalyticsByCustomerUid(String uid);

    Orders findByOrderUid(String orderId);

    Page<Orders> findByCollectorUidAndStatus(String uid, String scheduled,Pageable pageable);

    @Query(
            value ="Select * from orders where collector_uid =:uid and category_id=(select id from categories where category=:category)"
            ,nativeQuery = true
    )
    List<Orders> findByCategoryAndCollectorUid(String category, String uid);

    @Query(
            value ="Select * from orders where customer_uid IN (select uid from user where city=:city)"
            ,nativeQuery = true
    )
    List<Orders> findAllByCity(String city);
}
