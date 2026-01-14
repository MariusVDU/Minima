package com.example.service;

import com.example.model.Inventorius;
import com.example.repository.InventoriusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoriusService {

    @Autowired
    private InventoriusRepository inventoriusRepository;

    public List<Inventorius> getAllInventorius() {
        return inventoriusRepository.findAll();
    }

    public Optional<Inventorius> getInventoriusById(Long id) {
        return inventoriusRepository.findById(id);
    }

    public Optional<Inventorius> getInventoriusByPrekeAndParduotuve(Long prekesId, Long parduotuvesId) {
        return inventoriusRepository.findByPrekesIdAndParduotuvesId(prekesId, parduotuvesId);
    }

    public List<Inventorius> getInventoriusByPreke(Long prekesId) {
        return inventoriusRepository.findByPrekesId(prekesId);
    }

    public List<Inventorius> getInventoriusByParduotuve(Long parduotuvesId) {
        return inventoriusRepository.findByParduotuvesId(parduotuvesId);
    }

    public List<Inventorius> getLowStockItems(Integer maxKiekis) {
        return inventoriusRepository.findByKiekisLessThanEqual(maxKiekis);
    }

    @Transactional
    public Inventorius createInventorius(Inventorius inventorius) {
        // Patikrinti ar jau egzistuoja toks įrašas
        Optional<Inventorius> existing = inventoriusRepository.findByPrekesIdAndParduotuvesId(
            inventorius.getPrekesId(), 
            inventorius.getParduotuvesId()
        );
        
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Inventoriaus įrašas šiai prekei ir parduotuvei jau egzistuoja");
        }
        
        if (inventorius.getKiekis() == null) {
            inventorius.setKiekis(0);
        }
        
        if (inventorius.getMinimalusKiekis() == null) {
            inventorius.setMinimalusKiekis(10);
        }
        
        inventorius.setPaskutinisAtnaujinimas(LocalDateTime.now());
        return inventoriusRepository.save(inventorius);
    }

    @Transactional
    public Inventorius updateInventorius(Long id, Inventorius inventoriusDetails) {
        Inventorius inventorius = inventoriusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventoriaus įrašas nerastas su id: " + id));

        inventorius.setKiekis(inventoriusDetails.getKiekis());
        inventorius.setMinimalusKiekis(inventoriusDetails.getMinimalusKiekis());
        inventorius.setPaskutinisAtnaujinimas(LocalDateTime.now());

        return inventoriusRepository.save(inventorius);
    }

    @Transactional
    public Inventorius updateKiekis(Long id, Integer kiekioPokytis) {
        Inventorius inventorius = inventoriusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventoriaus įrašas nerastas su id: " + id));
        
        int naujas_kiekis = inventorius.getKiekis() + kiekioPokytis;
        if (naujas_kiekis < 0) {
            throw new IllegalArgumentException("Kiekis negali būti neigiamas");
        }
        
        inventorius.setKiekis(naujas_kiekis);
        inventorius.setPaskutinisAtnaujinimas(LocalDateTime.now());
        return inventoriusRepository.save(inventorius);
    }

    @Transactional
    public Inventorius updateKiekisByPrekeAndParduotuve(Long prekesId, Long parduotuvesId, BigDecimal kiekioPokytis) {
        Inventorius inventorius = inventoriusRepository.findByPrekesIdAndParduotuvesId(prekesId, parduotuvesId)
                .orElseThrow(() -> new RuntimeException("Inventoriaus įrašas nerastas prekei " + prekesId + " parduotuvėje " + parduotuvesId));
        
        int naujas_kiekis = inventorius.getKiekis() + kiekioPokytis.intValue();
        if (naujas_kiekis < 0) {
            throw new IllegalArgumentException("Nepakankamas kiekis inventoriuje. Turime: " + inventorius.getKiekis() + ", bandoma parduoti: " + kiekioPokytis.abs().intValue());
        }
        
        inventorius.setKiekis(naujas_kiekis);
        inventorius.setPaskutinisAtnaujinimas(LocalDateTime.now());
        return inventoriusRepository.save(inventorius);
    }

    @Transactional
    public void deleteInventorius(Long id) {
        if (!inventoriusRepository.existsById(id)) {
            throw new RuntimeException("Inventoriaus įrašas nerastas su id: " + id);
        }
        inventoriusRepository.deleteById(id);
    }
}
