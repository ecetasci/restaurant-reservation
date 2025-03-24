package com.ecetasci.restaurantrezervationapp.config;

import com.ecetasci.restaurantrezervationapp.entity.Restaurant;
import com.ecetasci.restaurantrezervationapp.entity.RestaurantTable;
import com.ecetasci.restaurantrezervationapp.repository.RestaurantRepository;
import com.ecetasci.restaurantrezervationapp.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;


    @Override
    public void run(String... args) {
      if (restaurantRepository.count() == 0) {//Bir kez çalıştırılıp restaurant oluşturuldu sonrasında tek olması için
            Restaurant savedRestaurant = new Restaurant();
            savedRestaurant.setName("Varsayılan Restoran");
            restaurantRepository.save(savedRestaurant);

            // Oluşturulan restoranın ID'sini terminale yazdır
            System.out.println("Oluşturulan Restoran ID: " +  savedRestaurant.getId());

            ArrayList<RestaurantTable> tables = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                RestaurantTable table = new RestaurantTable();
                table.setRestaurant(savedRestaurant);
                //table.setReservations(new ArrayList<>());
                tables.add(table);
            }

            restaurantTableRepository.saveAll(tables);

            // Oluşturulan masaların ID'lerini terminalde göster
            System.out.println("Oluşturulan Masa ID'leri:");
            tables.forEach(table ->
                    System.out.println("Table ID: " + table.getRestaurantTableId())
            );

            System.out.println("Varsayılan restoran ve 10 masa başarıyla oluşturuldu.");
       }
    }
}



