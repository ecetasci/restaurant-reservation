package com.ecetasci.restaurantrezervationapp.controller;

import com.ecetasci.restaurantrezervationapp.dto.AdminDto;
import com.ecetasci.restaurantrezervationapp.dto.ReservationDto;
import com.ecetasci.restaurantrezervationapp.entity.Admin;
import com.ecetasci.restaurantrezervationapp.service.AdminService;
import com.ecetasci.restaurantrezervationapp.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @Autowired
    ReservationService reservationService;

    @GetMapping("/{id}")
    public Admin getAdmin(@PathVariable Long id) {
        return adminService.getAdminById(id);
    }

    @PostMapping("/save")
    public Long saveAdmin(@RequestBody AdminDto adminDto){
        return adminService.save(adminDto);
    }

    // Admin login kontrolüyle rezervasyonları listeleme
    @PostMapping("/reservations")
    public ResponseEntity<List<ReservationDto>> getAllReservations(@RequestBody AdminDto adminDto) {
        List<ReservationDto> reservations = reservationService.getAllDtos(adminDto);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/password")
    public String changePassword (@RequestBody AdminDto adminDto, String newPassword){
        return adminService.updateAdminPassword(adminDto,newPassword);
    }



}
