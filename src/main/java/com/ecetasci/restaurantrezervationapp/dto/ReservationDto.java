package com.ecetasci.restaurantrezervationapp.dto;

import com.ecetasci.restaurantrezervationapp.entity.Restaurant;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationDto {

    private Long id;

    private Long restaurantId;

    private String customerName;

    private String customerPhoneNumber;

    private String customerEmail;

    private Long peopleCounts;

    private LocalDateTime reservationTime;

    private String description;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
