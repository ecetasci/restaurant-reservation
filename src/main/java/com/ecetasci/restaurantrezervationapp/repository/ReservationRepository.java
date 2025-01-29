package com.ecetasci.restaurantrezervationapp.repository;

import com.ecetasci.restaurantrezervationapp.entity.Reservation;
import com.ecetasci.restaurantrezervationapp.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r WHERE r.restaurant.id=:restaurantId")
    List<Reservation> findReservationByRestaurantId(@Param("restaurantId") Long id);
}
