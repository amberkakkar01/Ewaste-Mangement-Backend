package com.groupfive.ewastemanagement.repository;

import com.groupfive.ewastemanagement.entity.AllPendingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AllPendingRequestRepo extends JpaRepository<AllPendingRequest, Long> {

    List<AllPendingRequest> findByOrderId(String uid);

    Page<AllPendingRequest> findByCollectorUidAndStatus(String uid, String pending, Pageable pageable);
}
