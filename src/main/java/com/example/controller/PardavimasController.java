package com.example.controller;

import com.example.model.Pardavimas;
import com.example.service.PardavimasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pardavimai")
public class PardavimasController {

    @Autowired
    private PardavimasService pardavimasService;

    @GetMapping
    public ResponseEntity<List<Pardavimas>> getAllPardavimai() {
        return ResponseEntity.ok(pardavimasService.getAllPardavimai());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pardavimas> getPardavimasById(@PathVariable Long id) {
        return pardavimasService.getPardavimasById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/parduotuve/{parduotuvesId}")
    public ResponseEntity<List<Pardavimas>> getPardavimaiByParduotuve(@PathVariable Long parduotuvesId) {
        return ResponseEntity.ok(pardavimasService.getPardavimaiByParduotuve(parduotuvesId));
    }

    @GetMapping("/darbuotojas/{darbuotojoId}")
    public ResponseEntity<List<Pardavimas>> getPardavimaiByDarbuotojas(@PathVariable Long darbuotojoId) {
        return ResponseEntity.ok(pardavimasService.getPardavimaiByDarbuotojas(darbuotojoId));
    }

    @GetMapping("/busena/{busena}")
    public ResponseEntity<List<Pardavimas>> getPardavimaiByBusena(@PathVariable String busena) {
        return ResponseEntity.ok(pardavimasService.getPardavimaiByBusena(busena));
    }

    @GetMapping("/period")
    public ResponseEntity<List<Pardavimas>> getPardavimaiByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(pardavimasService.getPardavimaiByPeriod(start, end));
    }

    @GetMapping("/parduotuve/{parduotuvesId}/period")
    public ResponseEntity<List<Pardavimas>> getPardavimaiByParduotuveAndPeriod(
            @PathVariable Long parduotuvesId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(pardavimasService.getPardavimaiByParduotuveAndPeriod(parduotuvesId, start, end));
    }

    @PostMapping
    public ResponseEntity<?> createPardavimas(@RequestBody Pardavimas pardavimas) {
        try {
            Pardavimas created = pardavimasService.createPardavimas(pardavimas);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePardavimas(@PathVariable Long id, @RequestBody Pardavimas pardavimasDetails) {
        try {
            Pardavimas updated = pardavimasService.updatePardavimas(id, pardavimasDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePardavimas(@PathVariable Long id) {
        try {
            pardavimasService.deletePardavimas(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
