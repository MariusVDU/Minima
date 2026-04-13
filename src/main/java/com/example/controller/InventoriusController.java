package com.example.controller;

import com.example.model.Inventorius;
import com.example.service.InventoriusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventorius")
public class InventoriusController {

    @Autowired
    private InventoriusService inventoriusService;

    @GetMapping
    public ResponseEntity<List<Inventorius>> getAllInventorius() {
        return ResponseEntity.ok(inventoriusService.getAllInventorius());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventorius> getInventoriusById(@PathVariable Long id) {
        return inventoriusService.getInventoriusById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/preke/{prekesId}")
    public ResponseEntity<List<Inventorius>> getInventoriusByPreke(@PathVariable Long prekesId) {
        return ResponseEntity.ok(inventoriusService.getInventoriusByPreke(prekesId));
    }

    @GetMapping("/parduotuve/{parduotuvesId}")
    public ResponseEntity<List<Inventorius>> getInventoriusByParduotuve(@PathVariable Long parduotuvesId) {
        return ResponseEntity.ok(inventoriusService.getInventoriusByParduotuve(parduotuvesId));
    }

    @GetMapping("/preke/{prekesId}/parduotuve/{parduotuvesId}")
    public ResponseEntity<Inventorius> getInventoriusByPrekeAndParduotuve(
            @PathVariable Long prekesId, 
            @PathVariable Long parduotuvesId) {
        return inventoriusService.getInventoriusByPrekeAndParduotuve(prekesId, parduotuvesId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventorius>> getLowStockItems(
            @RequestParam(defaultValue = "10") Integer maxKiekis) {
        return ResponseEntity.ok(inventoriusService.getLowStockItems(maxKiekis));
    }

    @PostMapping
    public ResponseEntity<?> createInventorius(@Valid @RequestBody Inventorius inventorius) {
        try {
            Inventorius created = inventoriusService.createInventorius(inventorius);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventorius(@PathVariable Long id, @Valid @RequestBody Inventorius inventoriusDetails) {
        try {
            Inventorius updated = inventoriusService.updateInventorius(id, inventoriusDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/kiekis")
    public ResponseEntity<?> updateKiekis(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer pokytis = request.get("pokytis");
            if (pokytis == null) {
                return ResponseEntity.badRequest().body("Reikalingas 'pokytis' parametras");
            }
            Inventorius updated = inventoriusService.updateKiekis(id, pokytis);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/preke/{prekesId}/parduotuve/{parduotuvesId}/kiekis")
    public ResponseEntity<?> updateKiekisByPrekeAndParduotuve(
            @PathVariable Long prekesId,
            @PathVariable Long parduotuvesId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer pokytis = request.get("pokytis");
            if (pokytis == null) {
                return ResponseEntity.badRequest().body("Reikalingas 'pokytis' parametras");
            }
            Inventorius updated = inventoriusService.updateKiekisByPrekeAndParduotuve(
                prekesId, parduotuvesId, new BigDecimal(pokytis));
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventorius(@PathVariable Long id) {
        try {
            inventoriusService.deleteInventorius(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
