package com.crp.warsztat.repository;

import com.crp.warsztat.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repozytorium ServiceTypeRepository
 * Umożliwia operacje CRUD na encji ServiceType (rodzaj usługi).
 */
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    // Tu możesz dodać własne metody np. szukania po nazwie usługi
}
