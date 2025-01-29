package com.ecetasci.restaurantrezervationapp.repository;

import com.ecetasci.restaurantrezervationapp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {


    Optional<Customer> findByName(String name);


    @Query(value = "select c from Customer c where c.name = :customerName and c.phoneNumber = :customerPhoneNumber")
    Optional<Customer> findCustomerByNameAndPhoneNumber(@Param("customerName") String customerName,
                                                        @Param("customerPhoneNumber") String customerPhoneNumber);
}
