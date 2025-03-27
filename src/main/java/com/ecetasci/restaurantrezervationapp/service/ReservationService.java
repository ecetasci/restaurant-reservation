package com.ecetasci.restaurantrezervationapp.service;

import com.ecetasci.restaurantrezervationapp.dto.AdminDto;
import com.ecetasci.restaurantrezervationapp.dto.ReservationDto;
import com.ecetasci.restaurantrezervationapp.entity.*;
import com.ecetasci.restaurantrezervationapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    AdminRepository adminRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ReservationDto getReservationById(Long id) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isPresent()) {
            Reservation reservation = reservationOptional.get();
            return getReservationDto(reservation);
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



    public List<Reservation> getAll(AdminDto adminDto) {
        Optional<Admin> admin = adminRepository.findByNameAndPassword(adminDto.getName(), adminDto.getPassword());
        if (admin.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin Bulunamadı");
        }
        return reservationRepository.findAll();
    }

    public List<ReservationDto> getAllDtos(AdminDto adminDto) {
        List<Reservation> entities = getAll(adminDto);
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
        dto.setPeopleCounts(reservation.getPeopleCount());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setDescription(reservation.getDescription());
        return dto;
    }




    @Transactional
    public Long createReservation(ReservationDto request) {
        validateAvailableTables(request);
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
            reservation.setDescription(request.getDescription());
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

        //
        List<Long> allTableIds = getAvailableTableIdsForGivenTime(request);


       // List<Long> allTableIds = getAllTableIds(request);
        //List<RestaurantTable> reservedTable = new ArrayList<>();// rezerve edilmiş masaları saklamak için kullanılacak.
        // Zaten rezerve edilmiş masa id'lerini ayrı tut
        List<Long> reservedTableIds = new ArrayList<>(getReservedTableIdsFromCrashedReservations(crashedReservations, new ArrayList<>()));

        // rezerve edilmişleri çıkar, kalanları müsait masalar olarak tut
        allTableIds.removeAll(reservedTableIds);

        // filterAvailableTableIds(reservedTableIds, allTableIds);

        checkAvailableTables(allTableIds);

        double neededTableCount = getNeededTableCount(request, reservation);

        System.out.println("Başlangıçta Müsait Masalar: " + allTableIds);
        System.out.println("Başlangıçta Müsait Masa Sayısı: " + allTableIds.size());

        if (allTableIds == null || allTableIds.size() < neededTableCount) {
            throw new IllegalArgumentException("Not enough available tables!");
        }

        int tableCount = (int) neededTableCount;

        try {// müsait masa varsa müsait masalardan rezerve masalara atama yap
            // Yeni rezervasyon yapılacak masa id'lerini ayrı listeye al
            List<Long> newlyReservedTableIds = new ArrayList<>();

            // if (allTableIds.size() >= tableCount) {
            // for (int i = 0; i < tableCount; i++) {
            //   System.out.println("Döngü: " + i);
            // Long tableId = allTableIds.remove(0);

            //System.out.println(tableId);
            //reservedTableIds.add(tableId);
            //}

            for (int i = 0; i < tableCount; i++) {
                System.out.println("Döngü: " + i);
                Long tableId = allTableIds.remove(0);
                System.out.println("Yeni rezerve edilen masa id: " + tableId);
                newlyReservedTableIds.add(tableId);
            }

            // System.out.println("Rezerve Edilen Masalar: " + reservedTableIds);
            //System.out.println("Güncellenmiş Müsait Masalar: " + allTableIds);


            System.out.println("Yeni Rezerve Edilen Masalar: " + newlyReservedTableIds);
            System.out.println("Güncellenmiş Müsait Masalar: " + allTableIds);

            restaurant.setRestaurantTableIds(allTableIds);

          //  System.out.println("Güncellenmiş Müsait Masalar requeste set edildi: " + allTableIds);
            // Yeni rezerve edilen masaları çek
            List<RestaurantTable> reservedTables = restaurantTableRepository.findAllById(newlyReservedTableIds);

            // List<RestaurantTable> restaurantTables = restaurantTableRepository.findAllById(reservedTableIds);
            // reservedTable.addAll(restaurantTables);

            reservation.setRestaurantTables(reservedTables);
            reservation.setPeopleCount(request.getPeopleCounts());
            reservation.setDescription(request.getDescription());

            Optional<Customer> existCustomer = getCustomerOptional(request);
            //kayıtlı musteriyse
            if (existCustomer.isPresent()) {
                reservation.setCustomer(existCustomer.get());
                //kayıtlı müşteri değilse
            } else {
                Customer customer = saveNewCustomer(request);
                reservation.setCustomer(customer);
                //  reservation.setReservedTables(reservedTable);
                //  saveReservation(reservation);
            }
            saveReservation(reservation);

            //}
        } catch (Exception e) {
            throw new RuntimeException("Hata");
        }
        return reservation.getReservationId();
    }

    private Customer saveNewCustomer(ReservationDto request) {
        Customer customer = new Customer();
        customer.setName(request.getCustomerName());
        customer.setPhoneNumber(request.getCustomerPhoneNumber());
        customer.setEmail(request.getCustomerEmail());
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

    //aı // Bu metot, rezervasyon talebinde bulunan kişi sayısına ve restoranın mevcut masa durumuna göre
    // rezervasyon yapılabilecek yeterli masa olup olmadığını kontrol eder.
    // Eğer yeterli masa yoksa hata fırlatır ve rezervasyonun yapılmasını engeller.
    private void validateAvailableTables(ReservationDto request) {
        List<RestaurantTable> allTables = restaurantTableRepository.findRestaurantTableByRestaurantId(request.getRestaurantId());
        int totalTableCount = allTables.size();

        List<Reservation> crashedReservations = reservationRepository
                .findReservationByRestaurantId(request.getRestaurantId()).stream()
                .filter(reservation -> !request.getReservationTime().isAfter(reservation.getReservationTime().minusHours(4))
                        && request.getReservationTime().isBefore(reservation.getReservationTime().plusHours(4))
                        || request.getReservationTime().isEqual(reservation.getReservationTime()))
                .toList();

        int reservedTableCount = crashedReservations.stream()
                .mapToInt(r -> r.getRestaurantTables().size())
                .sum();

        double neededTableCount = Math.ceil(request.getPeopleCounts() / 4.0);

        if ((totalTableCount - reservedTableCount) < neededTableCount) {
            throw new IllegalArgumentException("Not enough available tables!");
        }
    }

    private List<Long> getAvailableTableIdsForGivenTime(ReservationDto request) {
        List<Long> allTableIds = getAllTableIds(request);

        // çakışan rezervasyonları al
        List<Reservation> crashedReservations = getCrashedReservations(
                request, reservationRepository.findReservationByRestaurantId(request.getRestaurantId())
        );

        // çakışan rezervasyonların masa id'lerini çıkar
        List<Long> reservedTableIds = getReservedTableIdsFromCrashedReservations(crashedReservations, new ArrayList<>());

        // müsait masaları döndür
        allTableIds.removeAll(reservedTableIds);

        return allTableIds;
    }

}






