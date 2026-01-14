package com.example.service;

import com.example.model.Pardavimas;
import com.example.model.PardavimoEilute;
import com.example.repository.PardavimasRepository;
import com.example.repository.PardavimoEiluteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PardavimasService {

    @Autowired
    private PardavimasRepository pardavimasRepository;

    @Autowired
    private PardavimoEiluteRepository pardavimoEiluteRepository;

    @Autowired
    private InventoriusService inventoriusService;

    public List<Pardavimas> getAllPardavimai() {
        return pardavimasRepository.findAll();
    }

    public Optional<Pardavimas> getPardavimasById(Long id) {
        return pardavimasRepository.findById(id);
    }

    public List<Pardavimas> getPardavimaiByParduotuve(Long parduotuvesId) {
        return pardavimasRepository.findByParduotuvesId(parduotuvesId);
    }

    public List<Pardavimas> getPardavimaiByDarbuotojas(Long darbuotojoId) {
        return pardavimasRepository.findByDarbuotojoId(darbuotojoId);
    }

    public List<Pardavimas> getPardavimaiByBusena(String busena) {
        return pardavimasRepository.findByBusena(busena);
    }

    public List<Pardavimas> getPardavimaiByPeriod(LocalDateTime start, LocalDateTime end) {
        return pardavimasRepository.findByDataLaikasBetween(start, end);
    }

    public List<Pardavimas> getPardavimaiByParduotuveAndPeriod(Long parduotuvesId, LocalDateTime start, LocalDateTime end) {
        return pardavimasRepository.findByParduotuvesIdAndDataLaikasBetween(parduotuvesId, start, end);
    }

    @Transactional
    public Pardavimas createPardavimas(Pardavimas pardavimas) {
        if (pardavimas.getDataLaikas() == null) {
            pardavimas.setDataLaikas(LocalDateTime.now());
        }
        if (pardavimas.getBendraSuma() == null) {
            pardavimas.setBendraSuma(BigDecimal.ZERO);
        }
        return pardavimasRepository.save(pardavimas);
    }

    @Transactional
    public Pardavimas updatePardavimas(Long id, Pardavimas pardavimasDetails) {
        Pardavimas pardavimas = pardavimasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pardavimas nerastas su id: " + id));

        pardavimas.setBusena(pardavimasDetails.getBusena());
        pardavimas.setBendraSuma(pardavimasDetails.getBendraSuma());

        return pardavimasRepository.save(pardavimas);
    }

    @Transactional
    public void deletePardavimas(Long id) {
        if (!pardavimasRepository.existsById(id)) {
            throw new RuntimeException("Pardavimas nerastas su id: " + id);
        }
        
        Pardavimas pardavimas = pardavimasRepository.findById(id).get();
        Long parduotuvesId = pardavimas.getParduotuvesId();
        
        // Gauti visas pardavimo eilutes ir grąžinti inventorių
        List<PardavimoEilute> eilutes = pardavimoEiluteRepository.findByPardavimoId(id);
        for (PardavimoEilute eilute : eilutes) {
            try {
                inventoriusService.updateKiekisByPrekeAndParduotuve(
                    eilute.getPrekesId(), 
                    parduotuvesId, 
                    eilute.getKiekis() // grąžiname kiekį
                );
            } catch (RuntimeException e) {
                // Jei inventoriaus įrašas nerastas, tiesiog ignoruojame
                System.err.println("Nepavyko grąžinti inventoriaus prekei " + eilute.getPrekesId() + ": " + e.getMessage());
            }
        }
        
        // Ištrinti visas pardavimo eilutes
        pardavimoEiluteRepository.deleteAll(eilutes);
        
        pardavimasRepository.deleteById(id);
    }

    @Transactional
    public BigDecimal recalculateBendraSuma(Long pardavimoId) {
        List<PardavimoEilute> eilutes = pardavimoEiluteRepository.findByPardavimoId(pardavimoId);
        BigDecimal bendraSuma = eilutes.stream()
                .map(PardavimoEilute::getSuma)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pardavimas pardavimas = pardavimasRepository.findById(pardavimoId)
                .orElseThrow(() -> new RuntimeException("Pardavimas nerastas su id: " + pardavimoId));
        pardavimas.setBendraSuma(bendraSuma);
        pardavimasRepository.save(pardavimas);

        return bendraSuma;
    }
}
