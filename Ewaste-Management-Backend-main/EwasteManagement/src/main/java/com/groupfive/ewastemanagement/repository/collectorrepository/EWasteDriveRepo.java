package com.groupfive.ewastemanagement.repository.collectorrepository;

import com.groupfive.ewastemanagement.entity.collectorentity.EWasteDrive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EWasteDriveRepo extends JpaRepository<EWasteDrive,Long> {

    List<EWasteDrive>getAllByCollectorEmail(String email);

    List<EWasteDrive> findByStatus(String status);

    EWasteDrive findByCollectorEmailAndId(String email, Long id);

    List<EWasteDrive> findAllByCity(String city);
}
