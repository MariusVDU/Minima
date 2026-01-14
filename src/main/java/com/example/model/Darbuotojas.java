package com.example.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "darbuotojai")
public class Darbuotojas {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String vardas;
    
    @Column(nullable = false, length = 100)
    private String pavarde;
    
    @Column(name = "asmens_kodas", nullable = false, unique = true, length = 20)
    private String asmensKodas;
    
    @Column(nullable = false, unique = true, length = 20)
    private String telefonas;
    
    @Column(name = "el_pastas", unique = true, length = 100)
    private String elPastas;
    
    @Column(name = "parduotuves_id", nullable = false)
    private Long parduotuvesId;
    
    @Column(name = "pareigu_id", nullable = false)
    private Long pareiguId;
    
    @Column(name = "idarbinimo_data", nullable = false)
    private LocalDate idarbinimoData;
    
    @Column(name = "valandinis_atlyginimas", nullable = false, precision = 10, scale = 2)
    private BigDecimal valandinisAtlyginimas;

    // Konstruktoriai
    public Darbuotojas() {}

    public Darbuotojas(String vardas, String pavarde, String asmensKodas, String telefonas, 
                       String elPastas, Long parduotuvesId, Long pareiguId, LocalDate idarbinimoData,
                       BigDecimal valandinisAtlyginimas) {
        this.vardas = vardas;
        this.pavarde = pavarde;
        this.asmensKodas = asmensKodas;
        this.telefonas = telefonas;
        this.elPastas = elPastas;
        this.parduotuvesId = parduotuvesId;
        this.pareiguId = pareiguId;
        this.idarbinimoData = idarbinimoData;
        this.valandinisAtlyginimas = valandinisAtlyginimas;
    }

    // Getters ir Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVardas() {
        return vardas;
    }

    public void setVardas(String vardas) {
        this.vardas = vardas;
    }

    public String getPavarde() {
        return pavarde;
    }

    public void setPavarde(String pavarde) {
        this.pavarde = pavarde;
    }

    public String getAsmensKodas() {
        return asmensKodas;
    }

    public void setAsmensKodas(String asmensKodas) {
        this.asmensKodas = asmensKodas;
    }

    public String getTelefonas() {
        return telefonas;
    }

    public void setTelefonas(String telefonas) {
        this.telefonas = telefonas;
    }

    public String getElPastas() {
        return elPastas;
    }

    public void setElPastas(String elPastas) {
        this.elPastas = elPastas;
    }

    public Long getParduotuvesId() {
        return parduotuvesId;
    }

    public void setParduotuvesId(Long parduotuvesId) {
        this.parduotuvesId = parduotuvesId;
    }

    public Long getPareiguId() {
        return pareiguId;
    }

    public void setPareiguId(Long pareiguId) {
        this.pareiguId = pareiguId;
    }

    public LocalDate getIdarbinimoData() {
        return idarbinimoData;
    }

    public void setIdarbinimoData(LocalDate idarbinimoData) {
        this.idarbinimoData = idarbinimoData;
    }

    public BigDecimal getValandinisAtlyginimas() {
        return valandinisAtlyginimas;
    }

    public void setValandinisAtlyginimas(BigDecimal valandinisAtlyginimas) {
        this.valandinisAtlyginimas = valandinisAtlyginimas;
    }

    @Override
    public String toString() {
        return "Darbuotojas{" +
                "id=" + id +
                ", vardas='" + vardas + '\'' +
                ", pavarde='" + pavarde + '\'' +
                ", asmensKodas='" + asmensKodas + '\'' +
                ", telefonas='" + telefonas + '\'' +
                ", elPastas='" + elPastas + '\'' +
                ", parduotuvesId=" + parduotuvesId +
                ", pareiguId=" + pareiguId +
                ", idarbinimoData=" + idarbinimoData +
                ", valandinisAtlyginimas=" + valandinisAtlyginimas +
                '}';
    }
}
