package com.ecetasci.restaurantrezervationapp.service;

import com.ecetasci.restaurantrezervationapp.dto.CustomerDto;
import com.ecetasci.restaurantrezervationapp.dto.ReservationDto;
import com.ecetasci.restaurantrezervationapp.entity.Customer;
import com.ecetasci.restaurantrezervationapp.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CustomerService {

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
}

