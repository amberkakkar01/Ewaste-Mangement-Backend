package com.groupfive.ewastemanagement.repository.collectorrepository;

import com.groupfive.ewastemanagement.entity.collectorentity.CategoriesAccepted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoriesAcceptedRepo extends JpaRepository<CategoriesAccepted,Long> {
    @Query(
            value ="Select uid from collector where id IN (select collector_id from categories_accepted where category_accepted=:category)"
            ,nativeQuery = true
    )
    List<String> findByCategoryAccepted(@Param("category") String category);

    @Query(
            value = "Select * from categories_accepted where category_accepted=:category and collector_id=:id",
            nativeQuery = true
    )
   List<CategoriesAccepted> findAllByCategoryAccepted(@Param("category") String category, @Param("id") Long id);

}
