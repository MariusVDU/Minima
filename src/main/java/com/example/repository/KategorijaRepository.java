package com.example.repository;

import com.example.model.Kategorija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KategorijaRepository extends JpaRepository<Kategorija, Long> {
    Optional<Kategorija> findByPavadinimas(String pavadinimas);
}
