package com.ecetasci.restaurantrezervationapp.dto;
import com.ecetasci.restaurantrezervationapp.entity.Customer;

public class CustomerDto {

    private long id;

    private String name;

    private String phoneNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
