package com.groupfive.ewastemanagement.repository;

import com.groupfive.ewastemanagement.entity.SellItems;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellItemRepo extends JpaRepository<SellItems,Long> {

    Page<SellItems> findAllByStatus(String status, Pageable pageable);
    List<SellItems> findAllByCollectorUidAndStatus(String uid,String status);
    List<SellItems> findByCategoryCategoryAcceptedAndCollectorUid(String category, String id);
    SellItems findByIdAndStatus(Long id,String status);
}
