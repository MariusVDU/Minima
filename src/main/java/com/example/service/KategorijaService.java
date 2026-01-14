package com.example.service;

import com.example.model.Kategorija;
import com.example.repository.KategorijaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KategorijaService {

    @Autowired
    private KategorijaRepository kategorijaRepository;

    public List<Kategorija> getAllKategorijos() {
        return kategorijaRepository.findAll();
    }

    public Optional<Kategorija> getKategorijaById(Long id) {
        return kategorijaRepository.findById(id);
    }

    public Optional<Kategorija> getKategorijaByPavadinimas(String pavadinimas) {
        return kategorijaRepository.findByPavadinimas(pavadinimas);
    }

    @Transactional
    public Kategorija createKategorija(Kategorija kategorija) {
        // Patikrinti ar pavadinimas unikalus
        if (kategorijaRepository.findByPavadinimas(kategorija.getPavadinimas()).isPresent()) {
            throw new IllegalArgumentException("Kategorija su tokiu pavadinimu jau egzistuoja");
        }
        return kategorijaRepository.save(kategorija);
    }

    @Transactional
    public Kategorija updateKategorija(Long id, Kategorija kategorijaDetails) {
        Kategorija kategorija = kategorijaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategorija nerasta su id: " + id));

        // Patikrinti pavadinimą
        Optional<Kategorija> existing = kategorijaRepository.findByPavadinimas(kategorijaDetails.getPavadinimas());
        if (existing.isPresent() && !existing.get().getKategorijosId().equals(id)) {
            throw new IllegalArgumentException("Kategorija su tokiu pavadinimu jau egzistuoja");
        }

        kategorija.setPavadinimas(kategorijaDetails.getPavadinimas());
        kategorija.setAprasymas(kategorijaDetails.getAprasymas());

        return kategorijaRepository.save(kategorija);
    }

    @Transactional
    public void deleteKategorija(Long id) {
        if (!kategorijaRepository.existsById(id)) {
            throw new RuntimeException("Kategorija nerasta su id: " + id);
        }
        kategorijaRepository.deleteById(id);
    }
}
