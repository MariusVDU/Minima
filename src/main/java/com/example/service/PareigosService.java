package com.example.service;

import com.example.model.Pareigos;
import com.example.repository.PareigosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PareigosService {

    @Autowired
    private PareigosRepository pareigosRepository;

    // Sukurti naujas pareigas
    public Pareigos createPareigos(Pareigos pareigos) {
        // Patikrinti ar pavadinimas jau egzistuoja
        if (pareigosRepository.existsByPavadinimas(pareigos.getPavadinimas())) {
            throw new IllegalArgumentException("Pareigos su tokiu pavadinimu jau egzistuoja");
        }
        
        return pareigosRepository.save(pareigos);
    }

    // Gauti visas pareigas
    public List<Pareigos> getAllPareigos() {
        return pareigosRepository.findAll();
    }

    // Gauti pareigas pagal ID
    public Optional<Pareigos> getPareigosById(Long id) {
        return pareigosRepository.findById(id);
    }

    // Gauti pareigas pagal pavadinimą
    public Optional<Pareigos> getPareigasByPavadinimas(String pavadinimas) {
        return pareigosRepository.findByPavadinimas(pavadinimas);
    }

    // Atnaujinti pareigas
    public Pareigos updatePareigos(Long id, Pareigos pareigosDetails) {
        Pareigos pareigos = pareigosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pareigos nerastos su ID: " + id));

        // Patikrinti pavadinimo unikalumą (jei keičiasi)
        if (!pareigosDetails.getPavadinimas().equals(pareigos.getPavadinimas()) &&
            pareigosRepository.existsByPavadinimas(pareigosDetails.getPavadinimas())) {
            throw new IllegalArgumentException("Pareigos su tokiu pavadinimu jau egzistuoja");
        }

        pareigos.setPavadinimas(pareigosDetails.getPavadinimas());
        pareigos.setAprasymas(pareigosDetails.getAprasymas());

        return pareigosRepository.save(pareigos);
    }

    // Ištrinti pareigas
    public void deletePareigos(Long id) {
        if (!pareigosRepository.existsById(id)) {
            throw new IllegalArgumentException("Pareigos nerastos su ID: " + id);
        }
        pareigosRepository.deleteById(id);
    }
}
