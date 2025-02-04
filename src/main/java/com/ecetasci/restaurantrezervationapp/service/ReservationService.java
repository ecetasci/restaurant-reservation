package com.ecetasci.restaurantrezervationapp.service;

import com.ecetasci.restaurantrezervationapp.dto.ReservationDto;
import com.ecetasci.restaurantrezervationapp.entity.Customer;
import com.ecetasci.restaurantrezervationapp.entity.Reservation;
import com.ecetasci.restaurantrezervationapp.entity.Restaurant;
import com.ecetasci.restaurantrezervationapp.entity.RestaurantTable;
import com.ecetasci.restaurantrezervationapp.repository.CustomerRepository;
import com.ecetasci.restaurantrezervationapp.repository.ReservationRepository;
import com.ecetasci.restaurantrezervationapp.repository.RestaurantRepository;
import com.ecetasci.restaurantrezervationapp.repository.RestaurantTableRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    RestaurantTableRepository restaurantTableRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation getReservationById(Long id) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isPresent()) {
            return reservationOptional.get();
        } else
            throw new NoSuchElementException("Reservation not found with id: " + id);
    }

    public Long saveReservation(Reservation reservation) {
        Reservation savedReservation = reservationRepository.save(reservation);
        Long id = savedReservation.getReservationId();
        return id;
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }


    @Transactional
    public Long createReservation(ReservationDto request) {

        List<Reservation> allReservations = getAll();
        long reservationCount = getReservationCount(request, allReservations);
        //Saat çakışmıyorsa
        if (reservationCount == 0) {
            return handleWithCurrentReservationZero(request);
            //Çakışan saatte isteniyorsa
        } else {
            return handleIfReservationExistGivenTime(request);
        }

    }

    private Long handleWithCurrentReservationZero(ReservationDto request) {

        try {
            Reservation reservation = new Reservation();
            Long requestedRestaurantId = request.getRestaurantId();
            Restaurant restaurant = getRestaurant(requestedRestaurantId);
            reservation.setRestaurant(restaurant);
            reservation.setReservationTime(request.getReservationTime());
            reservation.setPeopleCount(request.getPeopleCounts());
            reservationRepository.save(reservation);

            Double neededTableCount = getNeededTableCount(request, reservation);

            List<RestaurantTable> allTables = getRestaurantTables(request);

            List<RestaurantTable> reservedTables = new ArrayList<>();//rezerve edilecek masalar

            List<Long> allTableIds = getAllTableIds(request);

            List<Long> reservedTableIds = new ArrayList<>();//rezerve edilecek masaIds

            //İstenen kadar veya daha fazla müsait masa varsa müsait masalardan rezerve masalara atama yap
            if (allTables.size() >= neededTableCount) {
                for (int i = 0; i < neededTableCount; i++) {
                    RestaurantTable restaurantTable = allTables.remove(0);
                    ensureReservationListExists(restaurantTable);
                    restaurantTable.getReservations().add(reservation);
                    reservedTables.add(restaurantTable);
                    System.out.println("Reserved Table ID: " + restaurantTable.getRestaurantTableId());

                    Long tableId = allTableIds.remove(0);
                    reservedTableIds.add(tableId);
                    System.out.println("Removed Table ID: " + tableId);
                }
                reservation.setRestaurantTables(reservedTables);
            }

            Optional<Customer> existCustomer = getCustomerOptional(request);

            //kayıtlı musteriyse
            if (existCustomer.isPresent()) {
                reservation.setCustomer(existCustomer.get());
                saveReservation(reservation);
                //kayıtlı müşteri değilse
            } else {
                Customer customer = saveNewCustomer(request);
                reservation.setCustomer(customer);
                saveReservation(reservation);
            }
            return reservation.getReservationId();

        } catch (Exception e) {
            throw e;
        }
    }

    private List<RestaurantTable> getRestaurantTables(ReservationDto request) {
        List<RestaurantTable> allTables = restaurantTableRepository.findRestaurantTableByRestaurantId
                (request.getRestaurantId());
        return allTables;
    }

    private static void ensureReservationListExists(RestaurantTable restaurantTable) {
        if (restaurantTable.getReservations() == null) {
            restaurantTable.setReservations(new ArrayList<>());
        }
    }

    private Long handleIfReservationExistGivenTime(ReservationDto request) {

        Long requestedRestaurantId = getRequestedRestaurantId(request);
        Restaurant restaurant = getRestaurant(requestedRestaurantId);
        List<Reservation> restaurantsReservations = reservationRepository.findReservationByRestaurantId(requestedRestaurantId);
        List<Reservation> crashedReservations = getCrashedReservations(request, restaurantsReservations);

        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setReservationTime(request.getReservationTime());

        List<Long> allTableIds = getAllTableIds(request);

        List<RestaurantTable> reservedTable = new ArrayList<>();// rezerve edilmiş masaları saklamak için kullanılacak.

        List<Long> reservedTableIds = new ArrayList<>(getReservedTableIdsFromCrashedReservations(crashedReservations, reservedTable));

        filterAvailableTableIds(reservedTableIds, allTableIds);

        checkAvailableTables(allTableIds);

        double neededTableCount = getNeededTableCount(request, reservation);

        System.out.println("Başlangıçta Müsait Masalar: " + allTableIds);
        System.out.println("Başlangıçta Müsait Masa Sayısı: " + allTableIds.size());

        if (allTableIds == null || allTableIds.size() < neededTableCount) {
            throw new IllegalArgumentException("Not enough available tables!");
        }
        int tableCount = (int) neededTableCount;
        try {// müsait masa varsa müsait masalardan rezerve masalara atama yap
            if (allTableIds.size() >= tableCount) {
                for (int i = 0; i < tableCount; i++) {
                    System.out.println("Döngü: " + i);
                    Long tableId = allTableIds.remove(0);
                    System.out.println(tableId);
                    reservedTableIds.add(tableId);

                }
                System.out.println("Rezerve Edilen Masalar: " + reservedTableIds);
                System.out.println("Güncellenmiş Müsait Masalar: " + allTableIds);
                List<RestaurantTable> restaurantTables = restaurantTableRepository.findAllById(reservedTableIds);
                reservedTable.addAll(restaurantTables);

                Optional<Customer> existCustomer = getCustomerOptional(request);
                //kayıtlı musteriyse
                if (existCustomer.isPresent()) {
                    reservation.setCustomer(existCustomer.get());
                    saveReservation(reservation);
                    //kayıtlı müşteri değilse
                } else {
                    Customer customer = saveNewCustomer(request);
                    reservation.setCustomer(customer);
                    saveReservation(reservation);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Hata");
        }
        return reservation.getReservationId();
    }

    private Customer saveNewCustomer(ReservationDto request) {
        Customer customer = new Customer();
        customer.setName(request.getCustomerName());
        customer.setPhoneNumber(request.getCustomerPhoneNumber());
        Customer savedcustomer = customerRepository.save(customer);
        return customer;
    }

    private Optional<Customer> getCustomerOptional(ReservationDto request) {
        return customerRepository.findCustomerByNameAndPhoneNumber(
                request.getCustomerName(), request.getCustomerPhoneNumber());
    }

    private static void checkAvailableTables(List<Long> allTableIds) {
        if (allTableIds.isEmpty()) {
            throw new RuntimeException("Boş Masa yok");
        }
    }

    private static void filterAvailableTableIds(List<Long> reservedTableIds, List<Long> allTableIds) {
        //Rezerve masa idlerini tüm masa idlerinden çıkaralım, eğer sonuç sıfırsa hiç masa kalmamıştır,
        for (Long reservedTableId : reservedTableIds) {
            if (allTableIds.contains(reservedTableId)) {
                allTableIds.remove(reservedTableId);
                System.out.println("Rezerve edilen masaIdleri müsait masalardan silindi");
            }
        }//allTableIds artık boş masa ıdlerini verir.
    }

    private List<Long> getAllTableIds(ReservationDto request) {
        List<RestaurantTable> allTable = getRestaurantTables(request);
        List<Long> allTableIds = new ArrayList<>();//istenilen restoranın tüm masalarının ıdsini listeye koyduk
        for (RestaurantTable allTables : allTable) {
            allTableIds.add(allTables.getRestaurantTableId());//tableIds listesinde restoranın tüm masa ID’leri var
        }
        return allTableIds;
    }

    private static Long getRequestedRestaurantId(ReservationDto request) {
        return request.getRestaurantId();
    }

    private static List<Long> getReservedTableIdsFromCrashedReservations(List<Reservation> crashedReservations, List<RestaurantTable> reservedTable) {
        // Önceden yapılan rezervasyonlardaki tüm masaları listeye ekle
        for (Reservation makedReservation : crashedReservations) {
            // crashedReservations listesi, belirli bir saat diliminde çakışan rezervasyonları içerir.
            // Her rezervasyonun kullandığı masaları `reservedTable` listesine ekliyoruz.
            reservedTable.addAll(makedReservation.getRestaurantTables());
        }

        //rezerve edilen masaların ıdlerini ayrı listeleyelim
        List<Long> reservedTableIds = reservedTable.stream().map(RestaurantTable::getRestaurantTableId).toList();
        return reservedTableIds;
    }

    private static double getNeededTableCount(ReservationDto request, Reservation reservation) {
        reservation.setPeopleCount(request.getPeopleCounts());
        double neededTableCount = Math.ceil(Double.valueOf(request.getPeopleCounts()) / 4);
        return neededTableCount;
    }

    private static List<Reservation> getCrashedReservations(ReservationDto request, List<Reservation> restaurantsAllreservations) {
        List<Reservation> crashedReservations = restaurantsAllreservations.stream().filter(reservation ->
                !request.getReservationTime().isAfter(reservation.getReservationTime().minusHours(4))
                        && request.getReservationTime().isBefore(reservation.getReservationTime().plusHours(4))
                        || request.getReservationTime().isEqual(reservation.getReservationTime())).toList();
        return crashedReservations;
    }

    private Restaurant getRestaurant(Long requestedRestaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(requestedRestaurantId);
        if (restaurant.isEmpty()) {
            throw new RuntimeException("Restaurant bulunamadı");
        }
        return restaurant.get();
    }


    private static long getReservationCount(ReservationDto request, List<Reservation> allReservations) {
        long reservationCount = allReservations.stream().filter(reservation ->
                        request.getReservationTime().isAfter(reservation.getReservationTime().minusHours(4))
                                && request.getReservationTime().isBefore(reservation.getReservationTime().plusHours(4))
                                || request.getReservationTime().isEqual(reservation.getReservationTime()))
                .count();
        return reservationCount;
    }
}






