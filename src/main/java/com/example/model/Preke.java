package com.example.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prekes")
public class Preke {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prekes_id")
    private Long prekesId;
    
    @Column(nullable = false, length = 200)
    private String pavadinimas;
    
    @Column(columnDefinition = "TEXT")
    private String aprasymas;
    
    @Column(name = "bruksninis_kodas", unique = true, length = 50)
    private String bruksninisKodas;
    
    @Column(name = "pirkimo_kaina", precision = 10, scale = 2)
    private BigDecimal pirkimoKaina;
    
    @Column(name = "pardavimo_kaina", nullable = false, precision = 10, scale = 2)
    private BigDecimal pardavimoKaina;
    
    @Column(name = "mato_vienetas", columnDefinition = "ENUM('vnt', 'kg')")
    private String matoVienetas;
    
    @Column(name = "kategorijos_id")
    private Long kategorijosId;

    // Konstruktoriai
    public Preke() {}

    public Preke(String pavadinimas, BigDecimal pardavimoKaina) {
        this.pavadinimas = pavadinimas;
        this.pardavimoKaina = pardavimoKaina;
    }

    // Getters ir Setters
    public Long getPrekesId() {
        return prekesId;
    }

    public void setPrekesId(Long prekesId) {
        this.prekesId = prekesId;
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

    public String getBruksninisKodas() {
        return bruksninisKodas;
    }

    public void setBruksninisKodas(String bruksninisKodas) {
        this.bruksninisKodas = bruksninisKodas;
    }

    public BigDecimal getPirkimoKaina() {
        return pirkimoKaina;
    }

    public void setPirkimoKaina(BigDecimal pirkimoKaina) {
        this.pirkimoKaina = pirkimoKaina;
    }

    public BigDecimal getPardavimoKaina() {
        return pardavimoKaina;
    }

    public void setPardavimoKaina(BigDecimal pardavimoKaina) {
        this.pardavimoKaina = pardavimoKaina;
    }

    public String getMatoVienetas() {
        return matoVienetas;
    }

    public void setMatoVienetas(String matoVienetas) {
        this.matoVienetas = matoVienetas;
    }

    public Long getKategorijosId() {
        return kategorijosId;
    }

    public void setKategorijosId(Long kategorijosId) {
        this.kategorijosId = kategorijosId;
    }

    @Override
    public String toString() {
        return "Preke{" +
                "prekesId=" + prekesId +
                ", pavadinimas='" + pavadinimas + '\'' +
                ", aprasymas='" + aprasymas + '\'' +
                ", bruksninisKodas='" + bruksninisKodas + '\'' +
                ", pirkimoKaina=" + pirkimoKaina +
                ", pardavimoKaina=" + pardavimoKaina +
                ", matoVienetas='" + matoVienetas + '\'' +
                ", kategorijosId=" + kategorijosId +
                '}';
    }
}
