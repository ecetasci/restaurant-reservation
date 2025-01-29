package com.ecetasci.restaurantrezervationapp.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String name;


    @OneToMany(mappedBy = "admin")
    private List<Restaurant> restaurant;

    public Admin() { }

    public Admin(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Admin(Long id, String name, List<Restaurant> restaurant) {
        this.id = id;
        this.name = name;
        this.restaurant = restaurant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Restaurant> getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(List<Restaurant> restaurant) {
        this.restaurant = restaurant;
    }
}
