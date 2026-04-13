package com.example.service;

import com.example.model.Kategorija;
import com.example.repository.KategorijaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Vienetiniai testai – KategorijaService
 * Modelis: Arrange-Act-Assert (AAA)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("KategorijaService vienetiniai testai")
class KategorijaServiceTest {

    @Mock
    private KategorijaRepository kategorijaRepository;

    @InjectMocks
    private KategorijaService kategorijaService;

    private Kategorija elektronika;

    @BeforeEach
    void setUp() {
        elektronika = new Kategorija("Elektronika", "Elektronikos prekės");
        elektronika.setKategorijosId(1L);
    }

    // =====================================================================
    // SUKŪRIMAS (CREATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai sukuria naują kategoriją")
    void createKategorija_naujaPavadinimas_grazinaSukurta() {
        // Arrange
        when(kategorijaRepository.findByPavadinimas("Elektronika")).thenReturn(Optional.empty());
        when(kategorijaRepository.save(any(Kategorija.class))).thenReturn(elektronika);

        // Act
        Kategorija rezultatas = kategorijaService.createKategorija(new Kategorija("Elektronika", "Elektronikos prekės"));

        // Assert
        assertThat(rezultatas).isNotNull();
        assertThat(rezultatas.getPavadinimas()).isEqualTo("Elektronika");
        assertThat(rezultatas.getAprasymas()).isEqualTo("Elektronikos prekės");
        verify(kategorijaRepository).save(any(Kategorija.class));
    }

    @Test
    @DisplayName("Neleidžia sukurti kategorijos su egzistuojančiu pavadinimu")
    void createKategorija_egzistuojantisPavadinimas_metoKlaida() {
        // Arrange
        when(kategorijaRepository.findByPavadinimas("Elektronika")).thenReturn(Optional.of(elektronika));

        // Act & Assert
        assertThatThrownBy(() -> kategorijaService.createKategorija(new Kategorija("Elektronika")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jau egzistuoja");
        verify(kategorijaRepository, never()).save(any());
    }

    // =====================================================================
    // SKAITYMAS (READ)
    // =====================================================================

    @Test
    @DisplayName("Grąžina visas kategorijas")
    void getAllKategorijos_yraKategorijos_grazinaSarasa() {
        // Arrange
        Kategorija maistas = new Kategorija("Maistas", "Maisto prekės");
        maistas.setKategorijosId(2L);
        when(kategorijaRepository.findAll()).thenReturn(Arrays.asList(elektronika, maistas));

        // Act
        List<Kategorija> rezultatas = kategorijaService.getAllKategorijos();

        // Assert
        assertThat(rezultatas).hasSize(2);
        assertThat(rezultatas).extracting(Kategorija::getPavadinimas)
                .containsExactlyInAnyOrder("Elektronika", "Maistas");
    }

    @Test
    @DisplayName("Randa kategoriją pagal egzistuojantį ID")
    void getKategorijaById_egzistuoja_grazina() {
        // Arrange
        when(kategorijaRepository.findById(1L)).thenReturn(Optional.of(elektronika));

        // Act
        Optional<Kategorija> rezultatas = kategorijaService.getKategorijaById(1L);

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getKategorijosId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Grąžina tuščią, kai kategorija neegzistuoja")
    void getKategorijaById_neegzistuoja_tuščias() {
        // Arrange
        when(kategorijaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Kategorija> rezultatas = kategorijaService.getKategorijaById(99L);

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    @Test
    @DisplayName("Randa kategoriją pagal pavadinimą")
    void getKategorijaByPavadinimas_egzistuoja_grazina() {
        // Arrange
        when(kategorijaRepository.findByPavadinimas("Elektronika")).thenReturn(Optional.of(elektronika));

        // Act
        Optional<Kategorija> rezultatas = kategorijaService.getKategorijaByPavadinimas("Elektronika");

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getPavadinimas()).isEqualTo("Elektronika");
    }

    @Test
    @DisplayName("Grąžina tuščią, kai pavadinimas neegzistuoja")
    void getKategorijaByPavadinimas_neegzistuoja_tuščias() {
        // Arrange
        when(kategorijaRepository.findByPavadinimas("Neegzistuoja")).thenReturn(Optional.empty());

        // Act
        Optional<Kategorija> rezultatas = kategorijaService.getKategorijaByPavadinimas("Neegzistuoja");

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    // =====================================================================
    // REDAGAVIMAS (UPDATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai atnaujina egzistuojančią kategoriją")
    void updateKategorija_egzistuoja_grazinaSatnaujinta() {
        // Arrange
        Kategorija naujaDuomenys = new Kategorija("Elektronika Pro", "Atnaujintas aprasymas");
        when(kategorijaRepository.findById(1L)).thenReturn(Optional.of(elektronika));
        when(kategorijaRepository.findByPavadinimas("Elektronika Pro")).thenReturn(Optional.empty());
        when(kategorijaRepository.save(any(Kategorija.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Kategorija rezultatas = kategorijaService.updateKategorija(1L, naujaDuomenys);

        // Assert
        assertThat(rezultatas.getPavadinimas()).isEqualTo("Elektronika Pro");
        assertThat(rezultatas.getAprasymas()).isEqualTo("Atnaujintas aprasymas");
        verify(kategorijaRepository).save(any(Kategorija.class));
    }

    @Test
    @DisplayName("Klaida atnaujinant neegzistuojančią kategoriją")
    void updateKategorija_neegzistuoja_metoKlaida() {
        // Arrange
        when(kategorijaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> kategorijaService.updateKategorija(99L, new Kategorija("Nauja")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nerasta");
    }

    @Test
    @DisplayName("Klaida atnaujinant su kitos kategorijos pavadinimu")
    void updateKategorija_dublikatosPavadinimas_metoKlaida() {
        // Arrange
        Kategorija kita = new Kategorija("Maistas");
        kita.setKategorijosId(2L);
        when(kategorijaRepository.findById(1L)).thenReturn(Optional.of(elektronika));
        when(kategorijaRepository.findByPavadinimas("Maistas")).thenReturn(Optional.of(kita));

        // Act & Assert
        assertThatThrownBy(() -> kategorijaService.updateKategorija(1L, new Kategorija("Maistas")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jau egzistuoja");
    }

    // =====================================================================
    // TRYNIMAS (DELETE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai ištrina egzistuojančią kategoriją")
    void deleteKategorija_egzistuoja_ištrina() {
        // Arrange
        when(kategorijaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(kategorijaRepository).deleteById(1L);

        // Act
        kategorijaService.deleteKategorija(1L);

        // Assert
        verify(kategorijaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Klaida trinant neegzistuojančią kategoriją")
    void deleteKategorija_neegzistuoja_metoKlaida() {
        // Arrange
        when(kategorijaRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> kategorijaService.deleteKategorija(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nerasta");
        verify(kategorijaRepository, never()).deleteById(any());
    }
}
