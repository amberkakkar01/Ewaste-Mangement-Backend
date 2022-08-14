package com.groupfive.ewastemanagement.repository;

import com.groupfive.ewastemanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Role findRoleByName(String name);

}
