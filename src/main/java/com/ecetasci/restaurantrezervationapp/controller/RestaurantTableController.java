package com.ecetasci.restaurantrezervationapp.controller;

import com.ecetasci.restaurantrezervationapp.dto.RestaurantTableDto;
import com.ecetasci.restaurantrezervationapp.entity.Restaurant;
import com.ecetasci.restaurantrezervationapp.entity.RestaurantTable;
import com.ecetasci.restaurantrezervationapp.service.RestaurantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant-table")
public class RestaurantTableController {
    @Autowired
    RestaurantTableService restaurantTableService;

    @PostMapping
    public Long saveRestaurantTable(@RequestBody RestaurantTableDto restaurantTable){
        Long id = restaurantTableService.saveRestaurantTable(restaurantTable);
        return id;
    }

    @GetMapping("/{id}")
    public RestaurantTable getRestaurantTable(@PathVariable Long id) {
        RestaurantTable restaurantTable = restaurantTableService.getRestaurantTableById(id);
        return restaurantTable;
    }



}
