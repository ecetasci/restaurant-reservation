package com.ecetasci.restaurantrezervationapp.service;

import com.ecetasci.restaurantrezervationapp.dto.AdminDto;
import com.ecetasci.restaurantrezervationapp.entity.Admin;
import com.ecetasci.restaurantrezervationapp.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public long save(AdminDto adminDto) {
        Admin admin = new Admin();
        admin.setName(adminDto.getName());

        Admin save = adminRepository.save(admin);
        return save.getId();
    }


    public Admin getAdminById(Long id) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            return admin.get();
        } else
            throw new NoSuchElementException("Admin not found with id:");
    }

    public String deleteAdmin(Long id) {
        if (adminRepository.existsById(id)) {
            adminRepository.deleteById(id);
            return "Admin deleted with id" + id;
        } else
            return ("Admin not found with id: " + id);
    }

   public String updateAdminPassword(AdminDto adminDto, String newPassword ){
       Admin admin = adminRepository.findByNameAndPassword(adminDto.getName(), adminDto.getPassword())
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin bulunamadı"));
        admin.setPassword(newPassword);
        adminRepository.save(admin);
        return "Parola değiştirildi";
   }


}
