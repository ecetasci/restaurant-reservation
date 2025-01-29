package com.ecetasci.restaurantrezervationapp.service;

import com.ecetasci.restaurantrezervationapp.entity.Restaurant;
import com.ecetasci.restaurantrezervationapp.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;
    public RestaurantService(){


    }
    public Restaurant getById(Long id){
        Optional <Restaurant> restaurant= restaurantRepository.findById(id);
        if (restaurant.isPresent()){
            return restaurant.get();
        }
        return null;
    }

    public Long saveRestaurant(Restaurant restaurant){
        Restaurant savedrestaurant=restaurantRepository.save(restaurant);
        return savedrestaurant.getId();
    }
}
