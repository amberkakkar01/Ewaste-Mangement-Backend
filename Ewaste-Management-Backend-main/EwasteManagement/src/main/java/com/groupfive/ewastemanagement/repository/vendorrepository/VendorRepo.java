package com.groupfive.ewastemanagement.repository.vendorrepository;

import com.groupfive.ewastemanagement.entity.vendorentity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepo extends JpaRepository<Vendor,Long> {
    Vendor findByEmail(String email);

    List<Vendor> findByCity(String city);
}
