package com.example.service;

import com.example.model.Darbuotojas;
import com.example.repository.DarbuotojasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DarbuotojasService {

    @Autowired
    private DarbuotojasRepository darbuotojasRepository;

    // Sukurti naują darbuotoją
    public Darbuotojas createDarbuotojas(Darbuotojas darbuotojas) {
        // Patikrinti ar asmens kodas jau egzistuoja
        if (darbuotojasRepository.existsByAsmensKodas(darbuotojas.getAsmensKodas())) {
            throw new IllegalArgumentException("Darbuotojas su tokiu asmens kodu jau egzistuoja");
        }
        
        // Patikrinti ar tel. numeris jau egzistuoja
        if (darbuotojasRepository.existsByTelefonas(darbuotojas.getTelefonas())) {
            throw new IllegalArgumentException("Darbuotojas su tokiu telefono numeriu jau egzistuoja");
        }
        
        // Patikrinti ar el. paštas jau egzistuoja (jei nurodytas)
        if (darbuotojas.getElPastas() != null && 
            !darbuotojas.getElPastas().isEmpty() &&
            darbuotojasRepository.existsByElPastas(darbuotojas.getElPastas())) {
            throw new IllegalArgumentException("Darbuotojas su tokiu el. paštu jau egzistuoja");
        }
        
        return darbuotojasRepository.save(darbuotojas);
    }

    // Gauti visus darbuotojus
    public List<Darbuotojas> getAllDarbuotojai() {
        return darbuotojasRepository.findAll();
    }

    // Gauti darbuotoją pagal ID
    public Optional<Darbuotojas> getDarbuotojasById(Long id) {
        return darbuotojasRepository.findById(id);
    }

    // Gauti darbuotojus pagal parduotuvę
    public List<Darbuotojas> getDarbuotojaiByParduotuve(Long parduotuvesId) {
        return darbuotojasRepository.findByParduotuvesId(parduotuvesId);
    }

    // Gauti darbuotojus pagal pareigas
    public List<Darbuotojas> getDarbuotojaiByPareigos(Long pareiguId) {
        return darbuotojasRepository.findByPareiguId(pareiguId);
    }

    // Gauti darbuotoją pagal asmens kodą
    public Optional<Darbuotojas> getDarbuotojasByAsmensKodas(String asmensKodas) {
        return darbuotojasRepository.findByAsmensKodas(asmensKodas);
    }

    // Atnaujinti darbuotoją
    public Darbuotojas updateDarbuotojas(Long id, Darbuotojas darbuotojasDetails) {
        Darbuotojas darbuotojas = darbuotojasRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Darbuotojas nerastas su ID: " + id));

        // Patikrinti asmens kodo unikalumą (jei keičiasi)
        if (!darbuotojasDetails.getAsmensKodas().equals(darbuotojas.getAsmensKodas()) &&
            darbuotojasRepository.existsByAsmensKodas(darbuotojasDetails.getAsmensKodas())) {
            throw new IllegalArgumentException("Darbuotojas su tokiu asmens kodu jau egzistuoja");
        }

        // Patikrinti tel. unikalumą (jei keičiasi)
        if (!darbuotojasDetails.getTelefonas().equals(darbuotojas.getTelefonas()) &&
            darbuotojasRepository.existsByTelefonas(darbuotojasDetails.getTelefonas())) {
            throw new IllegalArgumentException("Darbuotojas su tokiu telefono numeriu jau egzistuoja");
        }

        // Patikrinti el. pašto unikalumą (jei keičiasi)
        if (darbuotojasDetails.getElPastas() != null && 
            !darbuotojasDetails.getElPastas().isEmpty() &&
            !darbuotojasDetails.getElPastas().equals(darbuotojas.getElPastas()) &&
            darbuotojasRepository.existsByElPastas(darbuotojasDetails.getElPastas())) {
            throw new IllegalArgumentException("Darbuotojas su tokiu el. paštu jau egzistuoja");
        }

        darbuotojas.setVardas(darbuotojasDetails.getVardas());
        darbuotojas.setPavarde(darbuotojasDetails.getPavarde());
        darbuotojas.setAsmensKodas(darbuotojasDetails.getAsmensKodas());
        darbuotojas.setTelefonas(darbuotojasDetails.getTelefonas());
        darbuotojas.setElPastas(darbuotojasDetails.getElPastas());
        darbuotojas.setParduotuvesId(darbuotojasDetails.getParduotuvesId());
        darbuotojas.setPareiguId(darbuotojasDetails.getPareiguId());
        darbuotojas.setIdarbinimoData(darbuotojasDetails.getIdarbinimoData());
        darbuotojas.setValandinisAtlyginimas(darbuotojasDetails.getValandinisAtlyginimas());

        return darbuotojasRepository.save(darbuotojas);
    }

    // Ištrinti darbuotoją
    public void deleteDarbuotojas(Long id) {
        if (!darbuotojasRepository.existsById(id)) {
            throw new IllegalArgumentException("Darbuotojas nerastas su ID: " + id);
        }
        darbuotojasRepository.deleteById(id);
    }
}
