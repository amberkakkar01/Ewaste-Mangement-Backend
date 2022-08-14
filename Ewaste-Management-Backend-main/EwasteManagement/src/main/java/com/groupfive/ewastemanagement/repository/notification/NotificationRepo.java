package com.groupfive.ewastemanagement.repository.notification;
import com.groupfive.ewastemanagement.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification,Long> {
    List<Notification> findByRoleAndStatus(String role, boolean status);

    List<Notification> findByRoleAndStatusAndCollectorUid(String role, boolean status, String uid);

    List<Notification> findByRoleAndStatusAndCustomerUid(String role, boolean status, String uid);
}
