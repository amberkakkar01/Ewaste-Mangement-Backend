package com.groupfive.ewastemanagement.repository.customerrepository;

import com.groupfive.ewastemanagement.entity.customerentity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface OrdersRepo extends JpaRepository<Orders,Long> {
    @Query(
            value ="Select * from orders where collector_uid IN (select uid from collector where uid=:uid)"
            ,nativeQuery = true
    )
    List<Orders> findByCollectorUid(String uid);

    List<Orders> findAllByCustomerUid(String uid);

    Orders findByOrderUid(String orderUid);

    List<Orders>findAllByCity(String city);

    @Query(
            value ="Select * from orders where collector_uid=:id and category=:category"
            ,nativeQuery = true
    )
    List<Orders> findByCategoryAndCollectorUid(@RequestParam("category") String category,@RequestParam("id") String id);
}
