package com.example.controller;

import com.example.model.Preke;
import com.example.service.PrekeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prekes")
public class PrekeController {

    @Autowired
    private PrekeService prekeService;

    @GetMapping
    public ResponseEntity<List<Preke>> getAllPrekes() {
        return ResponseEntity.ok(prekeService.getAllPrekes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Preke> getPrekeById(@PathVariable Long id) {
        return prekeService.getPrekeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bruksninis/{kodas}")
    public ResponseEntity<Preke> getPrekeByBruksninis(@PathVariable String kodas) {
        return prekeService.getPrekeByBruksninis(kodas)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Preke>> searchPrekes(@RequestParam String pavadinimas) {
        return ResponseEntity.ok(prekeService.searchPrekesByPavadinimas(pavadinimas));
    }

    @GetMapping("/kategorija/{kategorijosId}")
    public ResponseEntity<List<Preke>> getPrekesByKategorija(@PathVariable Long kategorijosId) {
        return ResponseEntity.ok(prekeService.getPrekesByKategorija(kategorijosId));
    }

    @PostMapping
    public ResponseEntity<?> createPreke(@RequestBody Preke preke) {
        try {
            Preke created = prekeService.createPreke(preke);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePreke(@PathVariable Long id, @RequestBody Preke prekeDetails) {
        try {
            Preke updated = prekeService.updatePreke(id, prekeDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePreke(@PathVariable Long id) {
        try {
            prekeService.deletePreke(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
