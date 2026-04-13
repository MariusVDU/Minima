package com.example.controller;

import com.example.model.PardavimoEilute;
import com.example.service.PardavimoEiluteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pardavimo-eilutes")
public class PardavimoEiluteController {

    @Autowired
    private PardavimoEiluteService pardavimoEiluteService;

    @GetMapping
    public ResponseEntity<List<PardavimoEilute>> getAllEilutes() {
        return ResponseEntity.ok(pardavimoEiluteService.getAllEilutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PardavimoEilute> getEiluteById(@PathVariable Long id) {
        return pardavimoEiluteService.getEiluteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pardavimas/{pardavimoId}")
    public ResponseEntity<List<PardavimoEilute>> getEilutesByPardavimas(@PathVariable Long pardavimoId) {
        return ResponseEntity.ok(pardavimoEiluteService.getEilutesByPardavimas(pardavimoId));
    }

    @GetMapping("/preke/{prekesId}")
    public ResponseEntity<List<PardavimoEilute>> getEilutesByPreke(@PathVariable Long prekesId) {
        return ResponseEntity.ok(pardavimoEiluteService.getEilutesByPreke(prekesId));
    }

    @PostMapping
    public ResponseEntity<?> createEilute(@Valid @RequestBody PardavimoEilute eilute) {
        try {
            PardavimoEilute created = pardavimoEiluteService.createEilute(eilute);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEilute(@PathVariable Long id, @Valid @RequestBody PardavimoEilute eiluteDetails) {
        try {
            PardavimoEilute updated = pardavimoEiluteService.updateEilute(id, eiluteDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEilute(@PathVariable Long id) {
        try {
            pardavimoEiluteService.deleteEilute(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
