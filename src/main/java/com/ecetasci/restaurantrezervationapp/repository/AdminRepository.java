package com.ecetasci.restaurantrezervationapp.repository;

import com.ecetasci.restaurantrezervationapp.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
