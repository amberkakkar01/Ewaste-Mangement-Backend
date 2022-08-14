package com.groupfive.ewastemanagement.repository;

import com.groupfive.ewastemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    User findUserByEmail(String email);

    User findUserByUid(String uid);

    @Query(
            value ="Select * from user where id IN (select user_id from user_role_mapping where role_id=(select id from role where name=:role))"
            ,nativeQuery = true
    )
    List<User>findAllUsersByRole(@RequestParam(name = "role") String role);

    @Query(
            value ="Select * from user where id IN (select user_id from user_role_mapping where role_id=(select id from role where name=:role)) and city=:city"
            ,nativeQuery = true
    )
    List<User>findAllUsersByRoleAndCity(@RequestParam(name = "role") String role,@RequestParam(name = "city") String city);

//    @Query(name = "select * from user where uid =(select uid from user_details where id = (select user_id from user_categories_mapping where category_id =(select id from categories where category=?1)))",
//            nativeQuery = true)
@Query(
        value ="select * from user where uid =(select uid from user_details where id = (select user_id from user_categories_mapping where category_id=(select id from categories where category=:category)))"
        ,nativeQuery = true
)
    List<User> findAllUsersByCat(@RequestParam(name = "category") String category);

}
