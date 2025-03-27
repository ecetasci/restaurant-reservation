package com.ecetasci.restaurantrezervationapp.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @OneToMany(mappedBy = "customer")
    private List<Reservation> reservations;


    public Customer() {
    }

    public Customer( String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Customer(Long id, String name, String phoneNumber, List<Reservation> reservations) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.reservations = reservations;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
