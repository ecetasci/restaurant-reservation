package com.ecetasci.restaurantrezervationapp.repository;

import com.ecetasci.restaurantrezervationapp.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {


    @Query("SELECT a FROM Admin a WHERE a.name = :name")
    Optional<Admin> getAdminByName(@Param("name") String name);


    @Query("SELECT a FROM Admin a WHERE a.name = :name AND a.password = :password")
    Optional<Admin> findByNameAndPassword(@Param("name") String name, @Param("password") String password);


}
