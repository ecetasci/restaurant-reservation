package com.ecetasci.restaurantrezervationapp.controller;

import com.ecetasci.restaurantrezervationapp.dto.CustomerDto;
import com.ecetasci.restaurantrezervationapp.entity.Customer;
import com.ecetasci.restaurantrezervationapp.repository.CustomerRepository;
import com.ecetasci.restaurantrezervationapp.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {


    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping("/save")
    public Long addCustomer(@RequestBody CustomerDto customer) {
        long id = customerService.saveCustomer(customer);
        return id;
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return customer;
    }
}
