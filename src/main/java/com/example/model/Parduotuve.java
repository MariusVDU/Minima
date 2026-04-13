package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "parduotuves")
public class Parduotuve {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false, length = 100)
    private String miestas;
    
    @NotBlank
    @Column(nullable = false, length = 255)
    private String gatve;
    
    @Column(unique = true, length = 20)
    private String telefonas;
    
    @Column(name = "el_pastas", unique = true, length = 100)
    private String elPastas;

    // Konstruktoriai
    public Parduotuve() {}

    public Parduotuve(String miestas, String gatve, String telefonas, String elPastas) {
        this.miestas = miestas;
        this.gatve = gatve;
        this.telefonas = telefonas;
        this.elPastas = elPastas;
    }

    // Getters ir Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMiestas() {
        return miestas;
    }

    public void setMiestas(String miestas) {
        this.miestas = miestas;
    }

    public String getGatve() {
        return gatve;
    }

    public void setGatve(String gatve) {
        this.gatve = gatve;
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

    @Override
    public String toString() {
        return "Parduotuve{" +
                "id=" + id +
                ", miestas='" + miestas + '\'' +
                ", gatve='" + gatve + '\'' +
                ", telefonas='" + telefonas + '\'' +
                ", elPastas='" + elPastas + '\'' +
                '}';
    }
}
