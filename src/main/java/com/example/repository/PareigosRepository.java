package com.example.repository;

import com.example.model.Pareigos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PareigosRepository extends JpaRepository<Pareigos, Long> {
    
    // Paieška pagal pavadinimą
    Optional<Pareigos> findByPavadinimas(String pavadinimas);
    
    // Tikrinti ar egzistuoja pagal pavadinimą
    boolean existsByPavadinimas(String pavadinimas);
}
