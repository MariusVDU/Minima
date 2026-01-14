package com.example.repository;

import com.example.model.Preke;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrekeRepository extends JpaRepository<Preke, Long> {
    Optional<Preke> findByBruksninisKodas(String bruksninisKodas);
    List<Preke> findByPavadinimasContainingIgnoreCase(String pavadinimas);
    List<Preke> findByKategorijosId(Long kategorijosId);
}
