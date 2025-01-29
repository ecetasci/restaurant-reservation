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

import java.time.LocalDate;
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
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations;
    }

    // Tüm rezervasyonları findAll ile çek
    // stream().filter kullanarak minusHours ve plusHours ekle
    // Eğer kayıt yoksa sorunsuz yeni rezervasyon oluştur
    // Varsa else düşecek orayı sonra yaparız
    @Transactional
    public Long createReservation(ReservationDto request) {

        List<Reservation> allReservations = reservationRepository.findAll();
        long reservationCount = allReservations.stream().filter(reservation ->
                        request.getReservationTime().isAfter(reservation.getReservationTime().minusHours(4))
                                && request.getReservationTime().isBefore(reservation.getReservationTime().plusHours(4))
                                || request.getReservationTime().isEqual(reservation.getReservationTime()))
                .count();
        //Çakışmıyorsa saat müsait oluştur
        if (reservationCount == 0) {
            try {
                Reservation reservation = new Reservation();
                Long requestedRestaurantId = request.getRestaurantId();
                //Kullanıcıdan gelen rsetaurantIdsi ile db den restaurantı çek
                Optional<Restaurant> restaurant = restaurantRepository.findById(requestedRestaurantId);
                if (restaurant.isEmpty()) {
                    throw new RuntimeException("Restaurant bulunamadı");
                }
                //Bulunan restaurantı rezervasyona ata
                reservation.setRestaurant(restaurant.get());

                //Rezervasyonu istenen restaurantın ıdsi ile dbden bu restaurantın masalarını çek listele,
                // çakışmadığı için bütün masalar müsait olduğundan tüm masaları çektim
                List<RestaurantTable> availableTables = restaurantTableRepository.findRestaurantTableByRestaurantId
                        (request.getRestaurantId());

                List<RestaurantTable> reservedTables = new ArrayList<>();//rezerve edilecek masalar

                //rezervasyonda talep edilen kişi sayısına göre kaç masa gerektiğini hesapla
                reservation.setPeopleCount(request.getPeopleCounts());
                Long neededTableCount = (request.getPeopleCounts() / 4) + (request.getPeopleCounts() % 4 == 0 ? 0 : 1);
                //İstenen kadar veya daha fazla müsait masa varsa müsait masalardan rezerve masalara atama yap
                if (availableTables.size() >= neededTableCount) {
                    for (int i = 0; i < neededTableCount; i++) {
                        reservedTables.add(availableTables.get(i));
                    }
                    reservation.setRestaurantTables(reservedTables);
                    availableTables.removeAll(reservedTables);
                }

                reservation.setReservationTime(request.getReservationTime());


                Optional<Customer> existCustomer = customerRepository.findCustomerByNameAndPhoneNumber(
                        request.getCustomerName(), request.getCustomerPhoneNumber());

                //kayıtlı musteriyse
                if (existCustomer.isPresent()) {
                    reservation.setCustomer(existCustomer.get());
                    Reservation savedReservation = reservationRepository.save(reservation);
                    return savedReservation.getReservationId();
                    //kayıtlı müşteri değilse
                } else {
                    Customer customer = new Customer();
                    customer.setName(request.getCustomerName());
                    customer.setPhoneNumber(request.getCustomerPhoneNumber());
                    Customer savedcustomer = customerRepository.save(customer);

                    reservation.setCustomer(customer);
                    Reservation savedReservation = reservationRepository.save(reservation);
                    return savedReservation.getReservationId();
                }
            } catch (Exception e) {
                throw e;
            }
            //************Çakışan saatte isteniyorsa***************************************************************
        } else {
            Reservation reservation = new Reservation();

            Long requestedRestaurantId = request.getRestaurantId();
            //Kullanıcıdan gelen rsetaurantIdsi ile db den restaurantı çek
            Optional<Restaurant> restaurant = restaurantRepository.findById(requestedRestaurantId);

            if (restaurant.isEmpty()) {
                throw new RuntimeException("Restaurant bulunamadı");
            }
            //Bulunan restaurantı rezervasyona ata
            reservation.setRestaurant(restaurant.get());

            //

                //bu restoranın bu saatlerdeki rezervasyonlarını bulalım
                List<Reservation> restaurantsAllreservations = reservationRepository.
                        (reservation ->
                                !request.getReservationTime().isAfter(reservation.getReservationTime().minusHours(4))
                                        && request.getReservationTime().isBefore(reservation.getReservationTime().plusHours(4))
                                        || request.getReservationTime().isEqual(reservation.getReservationTime())).toList();





            List<RestaurantTable> allTable = restaurantTableRepository.findRestaurantTableByRestaurantId
                    (request.getRestaurantId());

            List<Long> allTableIds = new ArrayList<>();//istenilen restoranın tüm masalarının ıdsini listeye koyduk
            for (RestaurantTable allTables : allTable) {
                allTableIds.add(allTables.getRestaurantTableId());//tableIds listesinde restoranın tüm masa ID’leri var
            }
                List<RestaurantTable> reservedTable = new ArrayList<>();// rezerve edilmiş masaları saklamak için kullanılacak.

                // Önceden yapılan rezervasyonlardaki tüm masaları listeye ekle
                for (Reservation makedReservation : restaurantsAllreservations) {
                    // `restaurantsAllreservations` listesi, belirli bir saat diliminde çakışan rezervasyonları içerir.
                    // Her rezervasyonun kullandığı masaları `reservedTable` listesine ekliyoruz.
                    reservedTable.addAll(makedReservation.getRestaurantTables());
                }

                //rezerve edilen masaların ıdlerini listeleyelim
                List<Long> reservedTableIds = reservedTable.stream().map(RestaurantTable::getRestaurantTableId).toList();

                //Rezerve masa idlerini tüm masa idlerinden çıkaralım, eğer sonuç sıfırsa hiç masa kalmamıştır,
                // sonuç 0dan büyükse müsait masa vardır
                for (Long reservedTableId : reservedTableIds) {
                    if (allTableIds.contains(reservedTableId)) {
                       allTableIds.remove(reservedTableId);
                        System.out.println("Rezerve edilen masaIdleri müsait masalardan silindi");
                    }
                }

                if (allTableIds.isEmpty()) {
                    throw new RuntimeException("Boş Masa yok");
                }
                //Boş masa varsa
                try {


                    //Kullanıcıdan gelen rsetaurantIdsi ile db den restaurantı çek
                    Optional<Restaurant> restaurant = restaurantRepository.findById(requestedRestaurantId);
                    if (restaurant.isEmpty()) {
                        throw new RuntimeException("Restaurant bulunamadı");
                    }
                    //Bulunan restaurantı rezervasyona ata
                    reservation.setRestaurant(restaurant.get());

                    //Rezervasyonu istenen restaurantın ıdsi ile dbden bu restaurantın masalarını çek listele,
                    // çakışmadığı için bütün masalar müsait olduğundan tüm masaları çektim
                    List<RestaurantTable> availableTables = restaurantTableRepository.findRestaurantTableByRestaurantId
                            (request.getRestaurantId());

                    List<RestaurantTable> reservedTables = new ArrayList<>();//rezerve edilecek masalar

                    //rezervasyonda talep edilen kişi sayısına göre kaç masa gerektiğini hesapla
                    reservation.setPeopleCount(request.getPeopleCounts());
                    Long neededTableCount = (request.getPeopleCounts() / 4) + (request.getPeopleCounts() % 4 == 0 ? 0 : 1);
                    //İstenen kadar veya daha fazla müsait masa varsa müsait masalardan rezerve masalara atama yap
                    if (availableTables.size() >= neededTableCount) {
                        for (int i = 0; i < neededTableCount; i++) {
                            reservedTables.add(availableTables.get(i));
                        }
                        reservation.setRestaurantTables(reservedTables);
                        availableTables.removeAll(reservedTables);
                    }

                    reservation.setReservationTime(request.getReservationTime());


                    Optional<Customer> existCustomer = customerRepository.findCustomerByNameAndPhoneNumber(
                            request.getCustomerName(), request.getCustomerPhoneNumber());

                    //kayıtlı musteriyse
                    if (existCustomer.isPresent()) {
                        reservation.setCustomer(existCustomer.get());
                        Reservation savedReservation = reservationRepository.save(reservation);
                        return savedReservation.getReservationId();
                        //kayıtlı müşteri değilse
                    } else {
                        Customer customer = new Customer();
                        customer.setName(request.getCustomerName());
                        customer.setPhoneNumber(request.getCustomerPhoneNumber());
                        Customer savedcustomer = customerRepository.save(customer);

                        reservation.setCustomer(customer);
                        Reservation savedReservation = reservationRepository.save(reservation);
                        return savedReservation.getReservationId();
                    }
                } catch (Exception e) {
                    throw e;
                }

                    //kişi sayısına yetecek kadar boş masa var mı kontrolü
                    //eğer varsa rezervasyonu oluşturalım //tableId artık boş masaları veriyor
                    Long avaibleTableCount = (long) allTableIds.size();
                    Long neededTableCount = (request.getPeopleCounts() / 4) + (request.getPeopleCounts() % 4 == 0 ? 0 : 1);

                    if (avaibleTableCount >= neededTableCount) {
                        Reservation reservation = new Reservation();
                        Long requestedRestaurantId = request.getRestaurantId();
                        Optional<Restaurant> restaurant = restaurantRepository.findById(requestedRestaurantId);
                        //if (restaurant.isEmpty()) {
                        //  throw new RuntimeException("Restaurant bulunamadı");
                        reservation.setRestaurant(restaurant.get());

                        Optional<Customer> existCustomer = customerRepository.findCustomerByNameAndPhoneNumber(
                                request.getCustomerName(), request.getCustomerPhoneNumber());
                        //kayıtlı musteriyse
                        if (existCustomer.isPresent()) {

                            reservation.setCustomer(existCustomer.get());
                            reservation.setReservationTime(request.getReservationTime());
                            reservation.setPeopleCount(request.getPeopleCounts());
                            Reservation savedReservation = reservationRepository.save(reservation);

                            return savedReservation.getReservationId();

                        } else {
                            Customer customer = new Customer();
                            customer.setName(request.getCustomerName());
                            customer.setPhoneNumber(request.getCustomerPhoneNumber());
                            Customer savedcustomer = customerRepository.save(customer);

                            reservation.setCustomer(customer);
                            reservation.setPeopleCount(request.getPeopleCounts());
                            reservation.setReservationTime(request.getReservationTime());

                            Reservation savedReservation = reservationRepository.save(reservation);
                            return savedReservation.getReservationId();
                        }

                    }

                } catch (Exception e) {
                    throw new RuntimeException("Müsait masa bulunamadı");

                }


            }
        }
        return null;
    }




