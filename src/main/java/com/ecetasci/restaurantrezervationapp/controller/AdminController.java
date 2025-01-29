package com.ecetasci.restaurantrezervationapp.controller;

import com.ecetasci.restaurantrezervationapp.entity.Admin;
import com.ecetasci.restaurantrezervationapp.entity.RestaurantTable;
import com.ecetasci.restaurantrezervationapp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @GetMapping("/{id}")
    public Admin getAdmin(@PathVariable Long id) {
        Admin admin = adminService.getAdminById(id);
        return admin;
    }

    @PostMapping
    public Long saveAdmin(@RequestBody Admin admin){
        Long id = adminService.save(admin);
        return id;
    }
}
