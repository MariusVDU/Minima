package com.example.service;

import com.example.model.Parduotuve;
import com.example.repository.ParduotuveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ParduotuveService {

    @Autowired
    private ParduotuveRepository parduotuveRepository;

    // Sukurti naują parduotuvę
    public Parduotuve createParduotuve(Parduotuve parduotuve) {
        // Patikrinti ar tel. numeris jau egzistuoja
        if (parduotuve.getTelefonas() != null && 
            parduotuveRepository.existsByTelefonas(parduotuve.getTelefonas())) {
            throw new IllegalArgumentException("Parduotuvė su tokiu telefono numeriu jau egzistuoja");
        }
        
        // Patikrinti ar el. paštas jau egzistuoja
        if (parduotuve.getElPastas() != null && 
            parduotuveRepository.existsByElPastas(parduotuve.getElPastas())) {
            throw new IllegalArgumentException("Parduotuvė su tokiu el. paštu jau egzistuoja");
        }
        
        return parduotuveRepository.save(parduotuve);
    }

    // Gauti visas parduotuves
    public List<Parduotuve> getAllParduotuves() {
        return parduotuveRepository.findAll();
    }

    // Gauti parduotuvę pagal ID
    public Optional<Parduotuve> getParduotuveById(Long id) {
        return parduotuveRepository.findById(id);
    }

    // Gauti parduotuves pagal miestą
    public List<Parduotuve> getParduotuvesByMiestas(String miestas) {
        return parduotuveRepository.findByMiestas(miestas);
    }

    // Atnaujinti parduotuvę
    public Parduotuve updateParduotuve(Long id, Parduotuve parduotuveDetails) {
        Parduotuve parduotuve = parduotuveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Parduotuvė nerasta su ID: " + id));

        // Patikrinti tel. unikalumą (jei keičiasi)
        if (parduotuveDetails.getTelefonas() != null && 
            !parduotuveDetails.getTelefonas().equals(parduotuve.getTelefonas()) &&
            parduotuveRepository.existsByTelefonas(parduotuveDetails.getTelefonas())) {
            throw new IllegalArgumentException("Parduotuvė su tokiu telefono numeriu jau egzistuoja");
        }

        // Patikrinti el. pašto unikalumą (jei keičiasi)
        if (parduotuveDetails.getElPastas() != null && 
            !parduotuveDetails.getElPastas().equals(parduotuve.getElPastas()) &&
            parduotuveRepository.existsByElPastas(parduotuveDetails.getElPastas())) {
            throw new IllegalArgumentException("Parduotuvė su tokiu el. paštu jau egzistuoja");
        }

        parduotuve.setMiestas(parduotuveDetails.getMiestas());
        parduotuve.setGatve(parduotuveDetails.getGatve());
        parduotuve.setTelefonas(parduotuveDetails.getTelefonas());
        parduotuve.setElPastas(parduotuveDetails.getElPastas());

        return parduotuveRepository.save(parduotuve);
    }

    // Ištrinti parduotuvę
    public void deleteParduotuve(Long id) {
        if (!parduotuveRepository.existsById(id)) {
            throw new IllegalArgumentException("Parduotuvė nerasta su ID: " + id);
        }
        parduotuveRepository.deleteById(id);
    }
}
