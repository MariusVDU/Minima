package com.example.controller;

import com.example.model.Parduotuve;
import com.example.service.ParduotuveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/parduotuves")
public class ParduotuveController {

    @Autowired
    private ParduotuveService parduotuveService;

    // GET /api/parduotuves - gauti visas parduotuves
    @GetMapping
    public ResponseEntity<List<Parduotuve>> getAllParduotuves() {
        List<Parduotuve> parduotuves = parduotuveService.getAllParduotuves();
        return ResponseEntity.ok(parduotuves);
    }

    // GET /api/parduotuves/{id} - gauti parduotuvę pagal ID
    @GetMapping("/{id}")
    public ResponseEntity<Parduotuve> getParduotuveById(@PathVariable Long id) {
        return parduotuveService.getParduotuveById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/parduotuves/miestas/{miestas} - gauti parduotuves pagal miestą
    @GetMapping("/miestas/{miestas}")
    public ResponseEntity<List<Parduotuve>> getParduotuvesByMiestas(@PathVariable String miestas) {
        List<Parduotuve> parduotuves = parduotuveService.getParduotuvesByMiestas(miestas);
        return ResponseEntity.ok(parduotuves);
    }

    // POST /api/parduotuves - sukurti naują parduotuvę
    @PostMapping
    public ResponseEntity<?> createParduotuve(@Valid @RequestBody Parduotuve parduotuve) {
        try {
            Parduotuve created = parduotuveService.createParduotuve(parduotuve);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/parduotuves/{id} - atnaujinti parduotuvę
    @PutMapping("/{id}")
    public ResponseEntity<?> updateParduotuve(@PathVariable Long id, @Valid @RequestBody Parduotuve parduotuveDetails) {
        try {
            Parduotuve updated = parduotuveService.updateParduotuve(id, parduotuveDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/parduotuves/{id} - ištrinti parduotuvę
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParduotuve(@PathVariable Long id) {
        try {
            parduotuveService.deleteParduotuve(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
