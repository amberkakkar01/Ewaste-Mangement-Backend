package com.groupfive.ewastemanagement.repository.vendorrepository;

import com.groupfive.ewastemanagement.entity.vendorentity.VendorOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorOrdersRepo extends JpaRepository<VendorOrders,Long> {

    List<VendorOrders> findAllByVendorUid(String uid);

    List<VendorOrders> findByCategoryAndCollectorUid(String category, String uid);

    List<VendorOrders> findAllByCollectorUidAndCategory(String uid, String temp);

    List<VendorOrders>findAllByCollectorUid(String uid);

}
