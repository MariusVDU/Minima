package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventorius", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"prekes_id", "parduotuves_id"}))
public class Inventorius {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventoriaus_id")
    private Long inventoriausId;
    
    @NotNull
    @Column(name = "prekes_id", nullable = false)
    private Long prekesId;
    
    @NotNull
    @Column(name = "parduotuves_id", nullable = false)
    private Long parduotuvesId;
    
    @Column(nullable = false)
    private Integer kiekis = 0;
    
    @Column(name = "minimalus_kiekis")
    private Integer minimalusKiekis = 10;
    
    @Column(name = "paskutinis_atnaujinimas")
    private LocalDateTime paskutinisAtnaujinimas;

    // Konstruktoriai
    public Inventorius() {
        this.paskutinisAtnaujinimas = LocalDateTime.now();
    }

    public Inventorius(Long prekesId, Long parduotuvesId, Integer kiekis) {
        this.prekesId = prekesId;
        this.parduotuvesId = parduotuvesId;
        this.kiekis = kiekis;
        this.paskutinisAtnaujinimas = LocalDateTime.now();
    }

    // Getters ir Setters
    public Long getInventoriausId() {
        return inventoriausId;
    }

    public void setInventoriausId(Long inventoriausId) {
        this.inventoriausId = inventoriausId;
    }

    public Long getPrekesId() {
        return prekesId;
    }

    public void setPrekesId(Long prekesId) {
        this.prekesId = prekesId;
    }

    public Long getParduotuvesId() {
        return parduotuvesId;
    }

    public void setParduotuvesId(Long parduotuvesId) {
        this.parduotuvesId = parduotuvesId;
    }

    public Integer getKiekis() {
        return kiekis;
    }

    public void setKiekis(Integer kiekis) {
        this.kiekis = kiekis;
        this.paskutinisAtnaujinimas = LocalDateTime.now();
    }

    public Integer getMinimalusKiekis() {
        return minimalusKiekis;
    }

    public void setMinimalusKiekis(Integer minimalusKiekis) {
        this.minimalusKiekis = minimalusKiekis;
    }

    public LocalDateTime getPaskutinisAtnaujinimas() {
        return paskutinisAtnaujinimas;
    }

    public void setPaskutinisAtnaujinimas(LocalDateTime paskutinisAtnaujinimas) {
        this.paskutinisAtnaujinimas = paskutinisAtnaujinimas;
    }

    @Override
    public String toString() {
        return "Inventorius{" +
                "inventoriausId=" + inventoriausId +
                ", prekesId=" + prekesId +
                ", parduotuvesId=" + parduotuvesId +
                ", kiekis=" + kiekis +
                ", minimalusKiekis=" + minimalusKiekis +
                ", paskutinisAtnaujinimas=" + paskutinisAtnaujinimas +
                '}';
    }
}
