package com.example.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pardavimai")
public class Pardavimas {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pardavimo_id")
    private Long pardavimoId;
    
    @Column(name = "parduotuves_id", nullable = false)
    private Long parduotuvesId;
    
    @Column(name = "darbuotojo_id", nullable = false)
    private Long darbuotojoId;
    
    @Column(name = "data_laikas", nullable = false)
    private LocalDateTime dataLaikas;
    
    @Column(name = "bendra_suma", nullable = false, precision = 10, scale = 2)
    private BigDecimal bendraSuma;
    
    @Column(length = 20)
    private String busena = "apmoketas";

    // Konstruktoriai
    public Pardavimas() {
        this.dataLaikas = LocalDateTime.now();
        this.bendraSuma = BigDecimal.ZERO;
    }

    public Pardavimas(Long parduotuvesId, Long darbuotojoId) {
        this.parduotuvesId = parduotuvesId;
        this.darbuotojoId = darbuotojoId;
        this.dataLaikas = LocalDateTime.now();
        this.bendraSuma = BigDecimal.ZERO;
        this.busena = "apmoketas";
    }

    // Getters ir Setters
    public Long getPardavimoId() {
        return pardavimoId;
    }

    public void setPardavimoId(Long pardavimoId) {
        this.pardavimoId = pardavimoId;
    }

    public Long getParduotuvesId() {
        return parduotuvesId;
    }

    public void setParduotuvesId(Long parduotuvesId) {
        this.parduotuvesId = parduotuvesId;
    }

    public Long getDarbuotojoId() {
        return darbuotojoId;
    }

    public void setDarbuotojoId(Long darbuotojoId) {
        this.darbuotojoId = darbuotojoId;
    }

    public LocalDateTime getDataLaikas() {
        return dataLaikas;
    }

    public void setDataLaikas(LocalDateTime dataLaikas) {
        this.dataLaikas = dataLaikas;
    }

    public BigDecimal getBendraSuma() {
        return bendraSuma;
    }

    public void setBendraSuma(BigDecimal bendraSuma) {
        this.bendraSuma = bendraSuma;
    }

    public String getBusena() {
        return busena;
    }

    public void setBusena(String busena) {
        this.busena = busena;
    }

    @Override
    public String toString() {
        return "Pardavimas{" +
                "pardavimoId=" + pardavimoId +
                ", parduotuvesId=" + parduotuvesId +
                ", darbuotojoId=" + darbuotojoId +
                ", dataLaikas=" + dataLaikas +
                ", bendraSuma=" + bendraSuma +
                ", busena='" + busena + '\'' +
                '}';
    }
}
