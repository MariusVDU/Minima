package com.example.repository;

import com.example.model.Parduotuve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParduotuveRepository extends JpaRepository<Parduotuve, Long> {
    
    // Paieška pagal miestą
    List<Parduotuve> findByMiestas(String miestas);
    
    // Paieška pagal telefoną
    Optional<Parduotuve> findByTelefonas(String telefonas);
    
    // Paieška pagal el. paštą
    Optional<Parduotuve> findByElPastas(String elPastas);
    
    // Tikrinti ar egzistuoja pagal telefoną
    boolean existsByTelefonas(String telefonas);
    
    // Tikrinti ar egzistuoja pagal el. paštą
    boolean existsByElPastas(String elPastas);
}
