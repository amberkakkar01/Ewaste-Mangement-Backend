package com.groupfive.ewastemanagement.repository.customerrepository;

import com.groupfive.ewastemanagement.entity.customerentity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepo extends JpaRepository<Customer,Long> {
    Customer findByEmail(String email);

    Customer findByUid(String customerUid);

    List<Customer> findByCity(String city);
}
