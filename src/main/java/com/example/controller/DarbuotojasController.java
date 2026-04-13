package com.example.controller;

import com.example.model.Darbuotojas;
import com.example.service.DarbuotojasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/darbuotojai")
public class DarbuotojasController {

    @Autowired
    private DarbuotojasService darbuotojasService;

    // GET /api/darbuotojai - gauti visus darbuotojus
    @GetMapping
    public ResponseEntity<List<Darbuotojas>> getAllDarbuotojai() {
        List<Darbuotojas> darbuotojai = darbuotojasService.getAllDarbuotojai();
        return ResponseEntity.ok(darbuotojai);
    }

    // GET /api/darbuotojai/{id} - gauti darbuotoją pagal ID
    @GetMapping("/{id}")
    public ResponseEntity<Darbuotojas> getDarbuotojasById(@PathVariable Long id) {
        return darbuotojasService.getDarbuotojasById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/darbuotojai/parduotuve/{parduotuvesId} - gauti darbuotojus pagal parduotuvę
    @GetMapping("/parduotuve/{parduotuvesId}")
    public ResponseEntity<List<Darbuotojas>> getDarbuotojaiByParduotuve(@PathVariable Long parduotuvesId) {
        List<Darbuotojas> darbuotojai = darbuotojasService.getDarbuotojaiByParduotuve(parduotuvesId);
        return ResponseEntity.ok(darbuotojai);
    }

    // GET /api/darbuotojai/pareigos/{pareiguId} - gauti darbuotojus pagal pareigas
    @GetMapping("/pareigos/{pareiguId}")
    public ResponseEntity<List<Darbuotojas>> getDarbuotojaiByPareigos(@PathVariable Long pareiguId) {
        List<Darbuotojas> darbuotojai = darbuotojasService.getDarbuotojaiByPareigos(pareiguId);
        return ResponseEntity.ok(darbuotojai);
    }

    // GET /api/darbuotojai/asmens-kodas/{asmensKodas} - gauti darbuotoją pagal asmens kodą
    @GetMapping("/asmens-kodas/{asmensKodas}")
    public ResponseEntity<Darbuotojas> getDarbuotojasByAsmensKodas(@PathVariable String asmensKodas) {
        return darbuotojasService.getDarbuotojasByAsmensKodas(asmensKodas)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/darbuotojai - sukurti naują darbuotoją
    @PostMapping
    public ResponseEntity<?> createDarbuotojas(@Valid @RequestBody Darbuotojas darbuotojas) {
        try {
            Darbuotojas created = darbuotojasService.createDarbuotojas(darbuotojas);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/darbuotojai/{id} - atnaujinti darbuotoją
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDarbuotojas(@PathVariable Long id, @Valid @RequestBody Darbuotojas darbuotojasDetails) {
        try {
            Darbuotojas updated = darbuotojasService.updateDarbuotojas(id, darbuotojasDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/darbuotojai/{id} - ištrinti darbuotoją
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDarbuotojas(@PathVariable Long id) {
        try {
            darbuotojasService.deleteDarbuotojas(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
