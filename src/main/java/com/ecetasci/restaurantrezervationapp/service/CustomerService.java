package com.ecetasci.restaurantrezervationapp.service;

import com.ecetasci.restaurantrezervationapp.dto.AdminDto;
import com.ecetasci.restaurantrezervationapp.dto.CustomerDto;
import com.ecetasci.restaurantrezervationapp.dto.ReservationDto;
import com.ecetasci.restaurantrezervationapp.entity.Admin;
import com.ecetasci.restaurantrezervationapp.entity.Customer;
import com.ecetasci.restaurantrezervationapp.entity.Reservation;
import com.ecetasci.restaurantrezervationapp.repository.CustomerRepository;
import com.ecetasci.restaurantrezervationapp.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private ReservationRepository reservationRepository;

    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    public long saveCustomer(CustomerDto customerDto) {
        Customer customer = new Customer();//nesne oluşturduktan sonra dtodan gelen parametreyle verilerle set ediyoruz.
        customer.setName(customerDto.getName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setEmail(customerDto.getEmail());

        Customer save = customerRepository.save(customer);
        return save.getId();
    }


    public List<Customer> findAll() {
        return customerRepository.findAll();
    }


    public Customer getCustomerById(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isPresent()) {
            return customerOptional.get();
        }
        throw new NoSuchElementException("Customer not found with id: " + id);
    }

    public Optional<Customer> getCustomerByName(String name) {
        return customerRepository.findByName(name);
    }

    public String getCustomerNameById(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isPresent()) {
            return customerOptional.get().getName();
        } else {
            throw new NoSuchElementException("Customer not found with id: " + id);
        }
    }

    public List<Reservation> getAll(CustomerDto customerDto) {
        Optional<Customer> customer = customerRepository.findCustomerByNameAndPhoneNumber(customerDto.getName(), customerDto.getPhoneNumber());
        if (customer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer Bulunamadı");
        }
        return reservationRepository.findReservationByCustomerName(customer.get().getName());
    }

    public List<ReservationDto> getAllDtos(CustomerDto customerDto) {
        List<Reservation> entities = getAll(customerDto);
        List<ReservationDto> response = new ArrayList<>();

        for (Reservation item : entities) {
            ReservationDto dto = getReservationDto(item);
            response.add(dto);
        }

        return response;
    }

    private ReservationDto getReservationDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getReservationId());
        dto.setRestaurantId(reservation.getRestaurant().getId());
        dto.setCustomerName(reservation.getCustomer().getName());
        dto.setCustomerPhoneNumber(reservation.getCustomer().getPhoneNumber());
        dto.setCustomerEmail(reservation.getEmail());
        dto.setPeopleCounts(reservation.getPeopleCount());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setDescription(reservation.getDescription());
        return dto;
    }
}

