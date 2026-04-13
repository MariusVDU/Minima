package com.example.service;

import com.example.model.Parduotuve;
import com.example.repository.ParduotuveRepository;
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
 * Vienetiniai testai – ParduotuveService
 * Modelis: Arrange-Act-Assert (AAA)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ParduotuveService vienetiniai testai")
class ParduotuveServiceTest {

    @Mock
    private ParduotuveRepository parduotuveRepository;

    @InjectMocks
    private ParduotuveService parduotuveService;

    private Parduotuve vilnius;

    @BeforeEach
    void setUp() {
        vilnius = new Parduotuve("Vilnius", "Gedimino pr. 1", "+37052000001", "vilnius@store.lt");
        vilnius.setId(1L);
    }

    // =====================================================================
    // SUKŪRIMAS (CREATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai sukuria naują parduotuvę")
    void createParduotuve_visiDuomenysUnikalus_grazinaSukurta() {
        // Arrange
        when(parduotuveRepository.existsByTelefonas("+37052000001")).thenReturn(false);
        when(parduotuveRepository.existsByElPastas("vilnius@store.lt")).thenReturn(false);
        when(parduotuveRepository.save(any(Parduotuve.class))).thenReturn(vilnius);

        // Act
        Parduotuve rezultatas = parduotuveService.createParduotuve(vilnius);

        // Assert
        assertThat(rezultatas).isNotNull();
        assertThat(rezultatas.getMiestas()).isEqualTo("Vilnius");
        verify(parduotuveRepository).save(any(Parduotuve.class));
    }

    @Test
    @DisplayName("Neleidžia sukurti parduotuvės su egzistuojančiu telefono numeriu")
    void createParduotuve_egzistuojantisTelefonas_metoKlaida() {
        // Arrange
        when(parduotuveRepository.existsByTelefonas("+37052000001")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> parduotuveService.createParduotuve(vilnius))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("telefono numeriu jau egzistuoja");
        verify(parduotuveRepository, never()).save(any());
    }

    @Test
    @DisplayName("Neleidžia sukurti parduotuvės su egzistuojančiu el. paštu")
    void createParduotuve_egzistuojantisElPastas_metoKlaida() {
        // Arrange
        when(parduotuveRepository.existsByTelefonas(anyString())).thenReturn(false);
        when(parduotuveRepository.existsByElPastas("vilnius@store.lt")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> parduotuveService.createParduotuve(vilnius))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("el. paštu jau egzistuoja");
    }

    // =====================================================================
    // SKAITYMAS (READ)
    // =====================================================================

    @Test
    @DisplayName("Grąžina visas parduotuves")
    void getAllParduotuves_yraParduotuves_grazinaSarasa() {
        // Arrange
        Parduotuve kaunas = new Parduotuve("Kaunas", "Laisvės al. 10", "+37037000001", "kaunas@store.lt");
        kaunas.setId(2L);
        when(parduotuveRepository.findAll()).thenReturn(Arrays.asList(vilnius, kaunas));

        // Act
        List<Parduotuve> rezultatas = parduotuveService.getAllParduotuves();

        // Assert
        assertThat(rezultatas).hasSize(2);
        assertThat(rezultatas).extracting(Parduotuve::getMiestas)
                .containsExactlyInAnyOrder("Vilnius", "Kaunas");
    }

    @Test
    @DisplayName("Randa parduotuvę pagal egzistuojantį ID")
    void getParduotuveById_egzistuoja_grazina() {
        // Arrange
        when(parduotuveRepository.findById(1L)).thenReturn(Optional.of(vilnius));

        // Act
        Optional<Parduotuve> rezultatas = parduotuveService.getParduotuveById(1L);

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Grąžina tuščią, kai parduotuvė neegzistuoja")
    void getParduotuveById_neegzistuoja_tuščias() {
        // Arrange
        when(parduotuveRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Parduotuve> rezultatas = parduotuveService.getParduotuveById(99L);

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    @Test
    @DisplayName("Filtravimas pagal miestą – grąžina parduotuves")
    void getParduotuvesByMiestas_egzistuoja_grazinaSarasa() {
        // Arrange
        when(parduotuveRepository.findByMiestas("Vilnius")).thenReturn(Arrays.asList(vilnius));

        // Act
        List<Parduotuve> rezultatas = parduotuveService.getParduotuvesByMiestas("Vilnius");

        // Assert
        assertThat(rezultatas).hasSize(1);
        assertThat(rezultatas.get(0).getMiestas()).isEqualTo("Vilnius");
    }

    @Test
    @DisplayName("Filtravimas pagal neegzistuojantį miestą – tuščias sąrašas")
    void getParduotuvesByMiestas_neegzistuoja_tuščiasSarasas() {
        // Arrange
        when(parduotuveRepository.findByMiestas("Klaipėda")).thenReturn(Arrays.asList());

        // Act
        List<Parduotuve> rezultatas = parduotuveService.getParduotuvesByMiestas("Klaipėda");

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    // =====================================================================
    // REDAGAVIMAS (UPDATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai atnaujina parduotuvę (ta pati kontaktinė informacija)")
    void updateParduotuve_keičiaTikAdresą_atnaujina() {
        // Arrange – naudojame tuos pačius telefoną ir el. paštą
        Parduotuve atnaujinta = new Parduotuve("Vilnius", "Pylimo g. 5", "+37052000001", "vilnius@store.lt");
        when(parduotuveRepository.findById(1L)).thenReturn(Optional.of(vilnius));
        when(parduotuveRepository.save(any(Parduotuve.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Parduotuve rezultatas = parduotuveService.updateParduotuve(1L, atnaujinta);

        // Assert
        assertThat(rezultatas.getGatve()).isEqualTo("Pylimo g. 5");
    }

    @Test
    @DisplayName("Klaida atnaujinant neegzistuojančią parduotuvę")
    void updateParduotuve_neegzistuoja_metoKlaida() {
        // Arrange
        when(parduotuveRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> parduotuveService.updateParduotuve(99L, vilnius))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nerasta");
    }

    @Test
    @DisplayName("Klaida atnaujinant su kitos parduotuvės telefono numeriu")
    void updateParduotuve_egzistuojantisTelefonasKitas_metoKlaida() {
        // Arrange
        Parduotuve atnaujinta = new Parduotuve("Vilnius", "Pylimo g. 5", "+37037000001", "vilnius@store.lt");
        when(parduotuveRepository.findById(1L)).thenReturn(Optional.of(vilnius));
        when(parduotuveRepository.existsByTelefonas("+37037000001")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> parduotuveService.updateParduotuve(1L, atnaujinta))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("telefono numeriu jau egzistuoja");
    }

    // =====================================================================
    // TRYNIMAS (DELETE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai ištrina egzistuojančią parduotuvę")
    void deleteParduotuve_egzistuoja_ištrina() {
        // Arrange
        when(parduotuveRepository.existsById(1L)).thenReturn(true);
        doNothing().when(parduotuveRepository).deleteById(1L);

        // Act
        parduotuveService.deleteParduotuve(1L);

        // Assert
        verify(parduotuveRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Klaida trinant neegzistuojančią parduotuvę")
    void deleteParduotuve_neegzistuoja_metoKlaida() {
        // Arrange
        when(parduotuveRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> parduotuveService.deleteParduotuve(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nerasta");
        verify(parduotuveRepository, never()).deleteById(any());
    }
}
