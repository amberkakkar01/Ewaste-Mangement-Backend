package com.groupfive.ewastemanagement.repository.collectorrepository;

import com.groupfive.ewastemanagement.entity.collectorentity.AllPendingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface AllPendingRequestRepo extends JpaRepository<AllPendingRequest, Long> {

    List<AllPendingRequest> findByCollectorUid(String uid);

    @Transactional
    @Query(name = "delete from all_pending_request where order_id=:orderId"
            , nativeQuery = true
    )
    void deleteAllByOrderId(@Param("orderId")String orderId);

    @Transactional
    void deleteByCollectorUidAndOrderId(String collectorUid,String orderUid);

    List<AllPendingRequest> findByOrderId(String uid);

    List<AllPendingRequest> findByCollectorUidAndStatus(String uid, String pending);
}
