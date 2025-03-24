package com.ecetasci.restaurantrezervationapp.service;

import com.ecetasci.restaurantrezervationapp.dto.RestaurantTableDto;
import com.ecetasci.restaurantrezervationapp.entity.Restaurant;
import com.ecetasci.restaurantrezervationapp.entity.RestaurantTable;
import com.ecetasci.restaurantrezervationapp.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantTableService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantService restaurantService;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository, RestaurantService restaurantService) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.restaurantService = restaurantService;
    }

    public Long saveRestaurantTable(RestaurantTableDto restaurantTableDto){
        RestaurantTable restaurantTable = new RestaurantTable();
        Restaurant restaurant = restaurantService.getById(restaurantTableDto.getRestaurantId());
        restaurantTable.setRestaurant(restaurant);
        RestaurantTable savedRestaurantTable = restaurantTableRepository.save(restaurantTable);
        return savedRestaurantTable.getRestaurantTableId();
    }

    public RestaurantTable getRestaurantTableById(Long id){
        Optional<RestaurantTable> restaurantTable = restaurantTableRepository.findById(id);
        return restaurantTable.orElse(null);
    }

}
