package com.ecetasci.restaurantrezervationapp.controller;

import com.ecetasci.restaurantrezervationapp.dto.AdminDto;
import com.ecetasci.restaurantrezervationapp.dto.ReservationDto;
import com.ecetasci.restaurantrezervationapp.entity.Reservation;
import com.ecetasci.restaurantrezervationapp.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/update")
    public Long update(@RequestBody ReservationDto reservationDto) {
        System.out.println(reservationDto.getPeopleCounts());
        return reservationService.updateReservation(reservationDto);
    }

    @PostMapping("/cancel")
    public Long delete(@RequestParam Long id) {
        return reservationService.delete(id);
    }

}
