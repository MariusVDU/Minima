package com.example.controller;

import com.example.model.Kategorija;
import com.example.service.KategorijaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kategorijos")
public class KategorijaController {

    @Autowired
    private KategorijaService kategorijaService;

    @GetMapping
    public ResponseEntity<List<Kategorija>> getAllKategorijos() {
        return ResponseEntity.ok(kategorijaService.getAllKategorijos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kategorija> getKategorijaById(@PathVariable Long id) {
        return kategorijaService.getKategorijaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pavadinimas/{pavadinimas}")
    public ResponseEntity<Kategorija> getKategorijaByPavadinimas(@PathVariable String pavadinimas) {
        return kategorijaService.getKategorijaByPavadinimas(pavadinimas)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createKategorija(@RequestBody Kategorija kategorija) {
        try {
            Kategorija created = kategorijaService.createKategorija(kategorija);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateKategorija(@PathVariable Long id, @RequestBody Kategorija kategorijaDetails) {
        try {
            Kategorija updated = kategorijaService.updateKategorija(id, kategorijaDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKategorija(@PathVariable Long id) {
        try {
            kategorijaService.deleteKategorija(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
