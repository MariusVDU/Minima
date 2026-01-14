package com.example.service;

import com.example.model.Preke;
import com.example.repository.PrekeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PrekeService {

    @Autowired
    private PrekeRepository prekeRepository;

    public List<Preke> getAllPrekes() {
        return prekeRepository.findAll();
    }

    public Optional<Preke> getPrekeById(Long id) {
        return prekeRepository.findById(id);
    }

    public Optional<Preke> getPrekeByBruksninis(String bruksninisKodas) {
        return prekeRepository.findByBruksninisKodas(bruksninisKodas);
    }

    public List<Preke> searchPrekesByPavadinimas(String pavadinimas) {
        return prekeRepository.findByPavadinimasContainingIgnoreCase(pavadinimas);
    }

    public List<Preke> getPrekesByKategorija(Long kategorijosId) {
        return prekeRepository.findByKategorijosId(kategorijosId);
    }

    @Transactional
    public Preke createPreke(Preke preke) {
        // Patikrinti ar brūkšninis kodas unikalus
        if (preke.getBruksninisKodas() != null && 
            prekeRepository.findByBruksninisKodas(preke.getBruksninisKodas()).isPresent()) {
            throw new IllegalArgumentException("Prekė su tokiu brūkšniniu kodu jau egzistuoja");
        }
        
        return prekeRepository.save(preke);
    }

    @Transactional
    public Preke updatePreke(Long id, Preke prekeDetails) {
        Preke preke = prekeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prekė nerasta su id: " + id));

        preke.setPavadinimas(prekeDetails.getPavadinimas());
        preke.setAprasymas(prekeDetails.getAprasymas());
        
        // Patikrinti brūkšninį kodą
        if (prekeDetails.getBruksninisKodas() != null) {
            Optional<Preke> existing = prekeRepository.findByBruksninisKodas(prekeDetails.getBruksninisKodas());
            if (existing.isPresent() && !existing.get().getPrekesId().equals(id)) {
                throw new IllegalArgumentException("Prekė su tokiu brūkšniniu kodu jau egzistuoja");
            }
            preke.setBruksninisKodas(prekeDetails.getBruksninisKodas());
        }
        
        preke.setPirkimoKaina(prekeDetails.getPirkimoKaina());
        preke.setPardavimoKaina(prekeDetails.getPardavimoKaina());
        preke.setMatoVienetas(prekeDetails.getMatoVienetas());
        preke.setKategorijosId(prekeDetails.getKategorijosId());

        return prekeRepository.save(preke);
    }

    @Transactional
    public void deletePreke(Long id) {
        if (!prekeRepository.existsById(id)) {
            throw new RuntimeException("Prekė nerasta su id: " + id);
        }
        prekeRepository.deleteById(id);
    }
}
