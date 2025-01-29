package com.ecetasci.restaurantrezervationapp.dto;

import com.ecetasci.restaurantrezervationapp.entity.Restaurant;

import java.time.LocalDateTime;

public class ReservationDto {
    private Long restaurantId;

    private String customerName;

    private String customerPhoneNumber;

    private Long peopleCounts;

    private LocalDateTime reservationTime;


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public Long getPeopleCounts() {
        return peopleCounts;
    }

    public void setPeopleCounts(Long peopleCounts) {
        this.peopleCounts = peopleCounts;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
