package com.ecetasci.restaurantrezervationapp.controller;

import com.ecetasci.restaurantrezervationapp.dto.ReservationDto;
import com.ecetasci.restaurantrezervationapp.entity.Reservation;
import com.ecetasci.restaurantrezervationapp.service.ReservationService;
import com.ecetasci.restaurantrezervationapp.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    @GetMapping("/{id}")
    public ReservationDto getReservation(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    @GetMapping("/list")
    public List<ReservationDto> getAllreservation() {
        return reservationService.getAllDtos();
    }

    @PostMapping("/create")
    public Long create(@RequestBody ReservationDto reservationDto) {
        return reservationService.createReservation(reservationDto);
    }



}
