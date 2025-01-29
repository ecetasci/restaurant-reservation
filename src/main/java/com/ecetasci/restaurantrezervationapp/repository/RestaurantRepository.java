package com.ecetasci.restaurantrezervationapp.repository;

import com.ecetasci.restaurantrezervationapp.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
