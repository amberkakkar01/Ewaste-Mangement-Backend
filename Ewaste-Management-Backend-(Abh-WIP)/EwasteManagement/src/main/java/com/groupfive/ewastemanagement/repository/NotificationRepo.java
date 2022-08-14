package com.groupfive.ewastemanagement.repository;
import com.groupfive.ewastemanagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification,Long> {

    List<Notification> findByRoleAndStatusAndCollectorUid(String role, boolean status, String uid);

    List<Notification> findByRoleAndStatusAndCustomerUid(String role, boolean status, String uid);
}
