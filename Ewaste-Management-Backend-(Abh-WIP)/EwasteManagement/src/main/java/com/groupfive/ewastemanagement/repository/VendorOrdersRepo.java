package com.groupfive.ewastemanagement.repository;

import com.groupfive.ewastemanagement.entity.VendorOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorOrdersRepo extends JpaRepository<VendorOrders,Long> {

    List<VendorOrders> findAllByVendorUid(String uid);
    
    @Query(
            value = "select * from vendor_orders where sales_id IN(select id from sell_items where collector_uid =:uid and category_id=(Select id from categories where category =:category))",
            nativeQuery = true
    )
    List<VendorOrders> findOrdersByCollectorUidAndCategory(String uid, String category);

    @Query(
            value = "Select * from vendor_orders where sales_id IN (Select id from sell_items where collector_uid =:uid)",
            nativeQuery = true
    )
    List<VendorOrders>findAllByCollectorUid(String uid);

}
