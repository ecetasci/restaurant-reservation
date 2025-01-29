package com.ecetasci.restaurantrezervationapp.service;

import com.ecetasci.restaurantrezervationapp.entity.RestaurantTable;
import com.ecetasci.restaurantrezervationapp.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantTableService {
    @Autowired
    RestaurantTableRepository restaurantTableRepository;

    public Long saveRestaurantTable(RestaurantTable restaurantTable){
        RestaurantTable savedRestaurantTable = restaurantTableRepository.save(restaurantTable);
        return savedRestaurantTable.getRestaurantTableId();
    }

    public RestaurantTable getRestaurantTableById(Long id){
        Optional<RestaurantTable> restaurantTable = restaurantTableRepository.findById(id);
        if (restaurantTable.isPresent()){
            return restaurantTable.get();
        }
        else
            return null;
    }

}
