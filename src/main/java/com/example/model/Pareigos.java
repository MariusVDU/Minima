package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "pareigos")
public class Pareigos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pareigu_id")
    private Long pareiguId;
    
    @NotBlank
    @Column(nullable = false, length = 100)
    private String pavadinimas;
    
    @Column(columnDefinition = "TEXT")
    private String aprasymas;

    // Konstruktoriai
    public Pareigos() {}

    public Pareigos(String pavadinimas) {
        this.pavadinimas = pavadinimas;
    }
    
    public Pareigos(String pavadinimas, String aprasymas) {
        this.pavadinimas = pavadinimas;
        this.aprasymas = aprasymas;
    }

    // Getters ir Setters
    public Long getPareiguId() {
        return pareiguId;
    }

    public void setPareiguId(Long pareiguId) {
        this.pareiguId = pareiguId;
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
        return "Pareigos{" +
                "pareiguId=" + pareiguId +
                ", pavadinimas='" + pavadinimas + '\'' +
                ", aprasymas='" + aprasymas + '\'' +
                '}';
    }
}
