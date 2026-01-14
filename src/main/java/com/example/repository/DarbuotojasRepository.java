package com.example.repository;

import com.example.model.Darbuotojas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DarbuotojasRepository extends JpaRepository<Darbuotojas, Long> {
    
    // Paieška pagal parduotuvę
    List<Darbuotojas> findByParduotuvesId(Long parduotuvesId);
    
    // Paieška pagal pareigas
    List<Darbuotojas> findByPareiguId(Long pareiguId);
    
    // Paieška pagal asmens kodą
    Optional<Darbuotojas> findByAsmensKodas(String asmensKodas);
    
    // Paieška pagal telefoną
    Optional<Darbuotojas> findByTelefonas(String telefonas);
    
    // Paieška pagal el. paštą
    Optional<Darbuotojas> findByElPastas(String elPastas);
    
    // Paieška pagal vardą ir pavardę
    List<Darbuotojas> findByVardasAndPavarde(String vardas, String pavarde);
    
    // Tikrinti ar egzistuoja pagal asmens kodą
    boolean existsByAsmensKodas(String asmensKodas);
    
    // Tikrinti ar egzistuoja pagal telefoną
    boolean existsByTelefonas(String telefonas);
    
    // Tikrinti ar egzistuoja pagal el. paštą
    boolean existsByElPastas(String elPastas);
}
