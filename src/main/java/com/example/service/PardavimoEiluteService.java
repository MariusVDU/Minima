package com.example.service;

import com.example.model.PardavimoEilute;
import com.example.repository.PardavimoEiluteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PardavimoEiluteService {

    @Autowired
    private PardavimoEiluteRepository pardavimoEiluteRepository;

    @Autowired
    private PardavimasService pardavimasService;

    @Autowired
    private InventoriusService inventoriusService;

    public List<PardavimoEilute> getAllEilutes() {
        return pardavimoEiluteRepository.findAll();
    }

    public Optional<PardavimoEilute> getEiluteById(Long id) {
        return pardavimoEiluteRepository.findById(id);
    }

    public List<PardavimoEilute> getEilutesByPardavimas(Long pardavimoId) {
        return pardavimoEiluteRepository.findByPardavimoId(pardavimoId);
    }

    public List<PardavimoEilute> getEilutesByPreke(Long prekesId) {
        return pardavimoEiluteRepository.findByPrekesId(prekesId);
    }

    @Transactional
    public PardavimoEilute createEilute(PardavimoEilute eilute) {
        // Apskaičiuoti sumą
        if (eilute.getSuma() == null && eilute.getKiekis() != null && eilute.getVienetoKaina() != null) {
            eilute.setSuma(eilute.getVienetoKaina().multiply(eilute.getKiekis()));
        }

        // Gauti pardavimo parduotuvės ID
        Long parduotuvesId = pardavimasService.getPardavimasById(eilute.getPardavimoId())
                .orElseThrow(() -> new RuntimeException("Pardavimas nerastas su id: " + eilute.getPardavimoId()))
                .getParduotuvesId();

        // Sumažinti inventorių
        try {
            inventoriusService.updateKiekisByPrekeAndParduotuve(
                eilute.getPrekesId(), 
                parduotuvesId, 
                eilute.getKiekis().negate() // neigiamas skaičius = mažinimas
            );
        } catch (RuntimeException e) {
            throw new RuntimeException("Inventoriaus klaida: " + e.getMessage());
        }

        PardavimoEilute saved = pardavimoEiluteRepository.save(eilute);

        // Perskaičiuoti pardavimo bendrą sumą
        pardavimasService.recalculateBendraSuma(eilute.getPardavimoId());

        return saved;
    }

    @Transactional
    public PardavimoEilute updateEilute(Long id, PardavimoEilute eiluteDetails) {
        PardavimoEilute eilute = pardavimoEiluteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pardavimo eilutė nerasta su id: " + id));

        eilute.setKiekis(eiluteDetails.getKiekis());
        eilute.setVienetoKaina(eiluteDetails.getVienetoKaina());
        
        // Perskaičiuoti sumą
        if (eilute.getKiekis() != null && eilute.getVienetoKaina() != null) {
            eilute.setSuma(eilute.getVienetoKaina().multiply(eilute.getKiekis()));
        }

        PardavimoEilute updated = pardavimoEiluteRepository.save(eilute);

        // Perskaičiuoti pardavimo bendrą sumą
        pardavimasService.recalculateBendraSuma(eilute.getPardavimoId());

        return updated;
    }

    @Transactional
    public void deleteEilute(Long id) {
        PardavimoEilute eilute = pardavimoEiluteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pardavimo eilutė nerasta su id: " + id));
        
        Long pardavimoId = eilute.getPardavimoId();
        
        // Grąžinti inventorių
        Long parduotuvesId = pardavimasService.getPardavimasById(pardavimoId)
                .orElseThrow(() -> new RuntimeException("Pardavimas nerastas su id: " + pardavimoId))
                .getParduotuvesId();
        
        try {
            inventoriusService.updateKiekisByPrekeAndParduotuve(
                eilute.getPrekesId(), 
                parduotuvesId, 
                eilute.getKiekis() // teigiamas skaičius = didinimas
            );
        } catch (RuntimeException e) {
            // Jei inventoriaus įrašas nerastas, tiesiog ignoruojame
            System.err.println("Nepavyko grąžinti inventoriaus: " + e.getMessage());
        }
        
        pardavimoEiluteRepository.deleteById(id);

        // Perskaičiuoti pardavimo bendrą sumą
        pardavimasService.recalculateBendraSuma(pardavimoId);
    }
}
