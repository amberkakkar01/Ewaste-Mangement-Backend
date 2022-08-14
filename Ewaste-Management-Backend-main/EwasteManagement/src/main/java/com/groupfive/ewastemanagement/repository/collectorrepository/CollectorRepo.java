package com.groupfive.ewastemanagement.repository.collectorrepository;

import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectorRepo extends JpaRepository<Collector,Long> {
    Collector findByEmail(String email);
    Collector findByUid(String uid);

    List<Collector>findByCity(String city);
}
