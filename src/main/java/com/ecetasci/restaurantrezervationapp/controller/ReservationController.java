package com.ecetasci.restaurantrezervationapp.controller;

import com.ecetasci.restaurantrezervationapp.dto.AdminDto;
import com.ecetasci.restaurantrezervationapp.dto.ReservationDto;
import com.ecetasci.restaurantrezervationapp.entity.Reservation;
import com.ecetasci.restaurantrezervationapp.service.ReservationService;
import com.ecetasci.restaurantrezervationapp.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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


    //RequestParam -> /api/admin?yas=34&isim=ece
    //Pathvariables -> /api/admin/{id} buradaki {id} pathvariables oluyor /api/admin/2
    //RequestBody -> POST method JSON datayı yakalamak

    @Deprecated
    @PostMapping("/list")
    public ResponseEntity<List<ReservationDto>> getAllreservation(@RequestBody AdminDto request) {
        List<ReservationDto> dtos = reservationService.getAllDtos(request);
        return ResponseEntity.ok(dtos);
    }


    @PostMapping("/create")
    public Long create(@RequestBody ReservationDto reservationDto) {
        return reservationService.createReservation(reservationDto);
    }

    @PostMapping("/save")
    @Deprecated
    public Long saveReservation(@RequestBody Reservation reservation) {
        return reservationService.saveReservation(reservation);
    }

}
