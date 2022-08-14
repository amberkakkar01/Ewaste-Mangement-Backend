package com.groupfive.ewastemanagement.repository;

import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoriesAcceptedRepo extends JpaRepository<CategoriesAccepted,Long> {

   @Query(
           value = "select * from categories where id =(select category_id from user_categories_mapping where category_id=(Select id from categories where category=:category) and user_id=:uid)",
           nativeQuery = true
   )
   List<CategoriesAccepted> findAllByCategoryAccepted(@Param("category") String category, @Param("uid") Long id);

   CategoriesAccepted findCategoriesAcceptedByCategoryAccepted(String category);

}
