package com.ecetasci.restaurantrezervationapp.controller;

import com.ecetasci.restaurantrezervationapp.dto.RestaurantDto;
import com.ecetasci.restaurantrezervationapp.entity.Restaurant;
import com.ecetasci.restaurantrezervationapp.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/{id}")
    public RestaurantDto getRestaurant(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getById(id);
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName(restaurant.getName());
        restaurantDto.setAddress(restaurant.getAddress());
        restaurantDto.setPhone(restaurant.getPhone());
        restaurantDto.setEmail(restaurant.getEmail());
        return restaurantDto;
    }

    // @PostMapping("/save")
    //public Long saveRestaurant(@RequestBody Restaurant restaurant) {
    //  Long id = restaurantService.saveRestaurant(restaurant);
    //return id;
    //}


}
