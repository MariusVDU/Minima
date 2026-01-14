package com.example.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pardavimo_eilutes")
public class PardavimoEilute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eilutes_id")
    private Long eilutesId;
    
    @Column(name = "pardavimo_id", nullable = false)
    private Long pardavimoId;
    
    @Column(name = "prekes_id", nullable = false)
    private Long prekesId;
    
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal kiekis;
    
    @Column(name = "vieneto_kaina", nullable = false, precision = 10, scale = 2)
    private BigDecimal vienetoKaina;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal suma;

    // Konstruktoriai
    public PardavimoEilute() {}

    public PardavimoEilute(Long pardavimoId, Long prekesId, BigDecimal kiekis, BigDecimal vienetoKaina) {
        this.pardavimoId = pardavimoId;
        this.prekesId = prekesId;
        this.kiekis = kiekis;
        this.vienetoKaina = vienetoKaina;
        this.suma = vienetoKaina.multiply(kiekis);
    }

    // Getters ir Setters
    public Long getEilutesId() {
        return eilutesId;
    }

    public void setEilutesId(Long eilutesId) {
        this.eilutesId = eilutesId;
    }

    public Long getPardavimoId() {
        return pardavimoId;
    }

    public void setPardavimoId(Long pardavimoId) {
        this.pardavimoId = pardavimoId;
    }

    public Long getPrekesId() {
        return prekesId;
    }

    public void setPrekesId(Long prekesId) {
        this.prekesId = prekesId;
    }

    public BigDecimal getKiekis() {
        return kiekis;
    }

    public void setKiekis(BigDecimal kiekis) {
        this.kiekis = kiekis;
        if (this.vienetoKaina != null && kiekis != null) {
            this.suma = this.vienetoKaina.multiply(kiekis);
        }
    }

    public BigDecimal getVienetoKaina() {
        return vienetoKaina;
    }

    public void setVienetoKaina(BigDecimal vienetoKaina) {
        this.vienetoKaina = vienetoKaina;
        if (this.kiekis != null && vienetoKaina != null) {
            this.suma = vienetoKaina.multiply(this.kiekis);
        }
    }

    public BigDecimal getSuma() {
        return suma;
    }

    public void setSuma(BigDecimal suma) {
        this.suma = suma;
    }

    @Override
    public String toString() {
        return "PardavimoEilute{" +
                "eilutesId=" + eilutesId +
                ", pardavimoId=" + pardavimoId +
                ", prekesId=" + prekesId +
                ", kiekis=" + kiekis +
                ", vienetoKaina=" + vienetoKaina +
                ", suma=" + suma +
                '}';
    }
}
