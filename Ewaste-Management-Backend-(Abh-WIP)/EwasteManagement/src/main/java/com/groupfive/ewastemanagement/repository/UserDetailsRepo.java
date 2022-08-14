package com.groupfive.ewastemanagement.repository;

import com.groupfive.ewastemanagement.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDetailsRepo extends JpaRepository<UserDetails,Long> {
    UserDetails findUserByUid(String uid);

    @Query(name = "select * from user_details where id = (select user_id from user_categories_mapping where category_id=:id")
    List<UserDetails> findUserDetailsByCategoriesAcceptedId(@Param("id") Long id);



}
