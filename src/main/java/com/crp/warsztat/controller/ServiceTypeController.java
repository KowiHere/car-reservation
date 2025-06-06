package com.crp.warsztat.controller;

import com.crp.warsztat.model.ServiceType;
import com.crp.warsztat.repository.ServiceTypeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Kontroler do obsługi typów usług (ServiceType).
 * Pozwala pobierać, dodawać i usuwać typy usług przez REST API.
 */
@RestController
@RequestMapping("/service-types")
public class ServiceTypeController {

    private final ServiceTypeRepository serviceTypeRepository;

    public ServiceTypeController(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    // Pobierz wszystkie typy usług (GET /service-types)
    @GetMapping
    public List<ServiceType> getAllServiceTypes() {
        return serviceTypeRepository.findAll();
    }

    // Pobierz jeden typ usługi po ID (GET /service-types/{id})
    @GetMapping("/{id}")
    public Optional<ServiceType> getServiceTypeById(@PathVariable Long id) {
        return serviceTypeRepository.findById(id);
    }

    // Dodaj nowy typ usługi (POST /service-types)
    @PostMapping
    public ServiceType createServiceType(@RequestBody ServiceType serviceType) {
        return serviceTypeRepository.save(serviceType);
    }

    // Usuń typ usługi (DELETE /service-types/{id})
    @DeleteMapping("/{id}")
    public void deleteServiceType(@PathVariable Long id) {
        serviceTypeRepository.deleteById(id);
    }
}
