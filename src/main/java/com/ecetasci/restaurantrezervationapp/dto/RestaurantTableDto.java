package com.ecetasci.restaurantrezervationapp.dto;

import java.io.Serializable;

public class RestaurantTableDto implements Serializable {
    private Long restaurantId;


    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
