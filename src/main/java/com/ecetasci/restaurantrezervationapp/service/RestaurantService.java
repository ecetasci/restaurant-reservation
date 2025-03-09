package com.ecetasci.restaurantrezervationapp.service;

import com.ecetasci.restaurantrezervationapp.dto.RestaurantDto;
import com.ecetasci.restaurantrezervationapp.entity.Restaurant;
import com.ecetasci.restaurantrezervationapp.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    public RestaurantService(RestaurantRepository restaurantRepository){
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant getById(Long id){
        Optional <Restaurant> restaurant= restaurantRepository.findById(id);
        return restaurant.orElse(null);
    }

    @Transactional
    public Long saveRestaurant(Restaurant restaurant){
        Restaurant savedrestaurant=restaurantRepository.save(restaurant);
        return savedrestaurant.getId();
    }

    @Transactional
    public Long saveRestaurant(RestaurantDto restaurant){
        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setName(restaurant.getName());
        newRestaurant.setAddress(restaurant.getAddress());
        newRestaurant.setPhone(restaurant.getPhone());
        newRestaurant.setEmail(restaurant.getEmail());
        newRestaurant.setTablenumber(restaurant.getTablenumber());
        Restaurant savedRestaurant = restaurantRepository.save(newRestaurant);
        return savedRestaurant.getId();
    }
}
