package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "kategorijos")
public class Kategorija {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kategorijos_id")
    private Long kategorijosId;
    
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String pavadinimas;
    
    @Column(columnDefinition = "TEXT")
    private String aprasymas;

    // Konstruktoriai
    public Kategorija() {}

    public Kategorija(String pavadinimas) {
        this.pavadinimas = pavadinimas;
    }

    public Kategorija(String pavadinimas, String aprasymas) {
        this.pavadinimas = pavadinimas;
        this.aprasymas = aprasymas;
    }

    // Getters ir Setters
    public Long getKategorijosId() {
        return kategorijosId;
    }

    public void setKategorijosId(Long kategorijosId) {
        this.kategorijosId = kategorijosId;
    }

    public String getPavadinimas() {
        return pavadinimas;
    }

    public void setPavadinimas(String pavadinimas) {
        this.pavadinimas = pavadinimas;
    }

    public String getAprasymas() {
        return aprasymas;
    }

    public void setAprasymas(String aprasymas) {
        this.aprasymas = aprasymas;
    }

    @Override
    public String toString() {
        return "Kategorija{" +
                "kategorijosId=" + kategorijosId +
                ", pavadinimas='" + pavadinimas + '\'' +
                ", aprasymas='" + aprasymas + '\'' +
                '}';
    }
}
