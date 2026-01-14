package com.example.controller;

import com.example.model.Pareigos;
import com.example.service.PareigosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pareigos")
public class PareigosController {

    @Autowired
    private PareigosService pareigosService;

    // GET /api/pareigos - gauti visas pareigas
    @GetMapping
    public ResponseEntity<List<Pareigos>> getAllPareigos() {
        List<Pareigos> pareigos = pareigosService.getAllPareigos();
        return ResponseEntity.ok(pareigos);
    }

    // GET /api/pareigos/{id} - gauti pareigas pagal ID
    @GetMapping("/{id}")
    public ResponseEntity<Pareigos> getPareigosById(@PathVariable Long id) {
        return pareigosService.getPareigosById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/pareigos/pavadinimas/{pavadinimas} - gauti pareigas pagal pavadinimą
    @GetMapping("/pavadinimas/{pavadinimas}")
    public ResponseEntity<Pareigos> getPareigasByPavadinimas(@PathVariable String pavadinimas) {
        return pareigosService.getPareigasByPavadinimas(pavadinimas)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/pareigos - sukurti naujas pareigas
    @PostMapping
    public ResponseEntity<?> createPareigos(@RequestBody Pareigos pareigos) {
        try {
            Pareigos created = pareigosService.createPareigos(pareigos);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/pareigos/{id} - atnaujinti pareigas
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePareigos(@PathVariable Long id, @RequestBody Pareigos pareigosDetails) {
        try {
            Pareigos updated = pareigosService.updatePareigos(id, pareigosDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/pareigos/{id} - ištrinti pareigas
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePareigos(@PathVariable Long id) {
        try {
            pareigosService.deletePareigos(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
