package com.ecetasci.restaurantrezervationapp.repository;

import com.ecetasci.restaurantrezervationapp.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {


    @Query("select rt from RestaurantTable rt WHERE rt.restaurant.id=:restaurantId")
    List<RestaurantTable> findRestaurantTableByRestaurantId(@Param("restaurantId") Long id);

}




