package com.groupfive.ewastemanagement.repository;

import com.groupfive.ewastemanagement.entity.EWasteDrive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EWasteDriveRepo extends JpaRepository<EWasteDrive,Long> {

    List<EWasteDrive> getAllEWasteAnalyticsByCollectorEmail(String email);

    List<EWasteDrive> findByStatus(String status);

    EWasteDrive findByCollectorEmailAndId(String email, Long id);

    List<EWasteDrive> findAllByCity(String city);

    Page<EWasteDrive> getAllByCollectorEmail(String email, Pageable pageable);
}
