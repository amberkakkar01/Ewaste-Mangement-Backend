package com.groupfive.ewastemanagement.repository.collectorrepository;

import com.groupfive.ewastemanagement.entity.collectorentity.SellItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SellItemRepo extends JpaRepository<SellItems,Long> {

    List<SellItems>findAllByStatus(String status);

    List<SellItems> findAllByCollectorUid(String uid);

    List<SellItems> findAllByCollectorUidAndStatus(String uid,String status);

    @Query(
            value ="Select * from sell_items where collector_uid=:id and category=:category"
            ,nativeQuery = true
    )
    List<SellItems> findByCategoryAndCollectorUid(@RequestParam("category") String category, @RequestParam("id") String id);

    SellItems findByIdAndStatus(Long id,String status);
}
