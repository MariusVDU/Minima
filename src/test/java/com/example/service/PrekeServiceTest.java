package com.example.service;

import com.example.model.Preke;
import com.example.repository.PrekeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Vienetiniai testai – PrekeService
 * Modelis: Arrange-Act-Assert (AAA)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PrekeService vienetiniai testai")
class PrekeServiceTest {

    @Mock
    private PrekeRepository prekeRepository;

    @InjectMocks
    private PrekeService prekeService;

    private Preke pienas;

    @BeforeEach
    void setUp() {
        pienas = new Preke("Pienas", new BigDecimal("1.50"));
        pienas.setPrekesId(1L);
        pienas.setBruksninisKodas("1234567890");
        pienas.setKategorijosId(1L);
        pienas.setMatoVienetas("vnt");
        pienas.setPirkimoKaina(new BigDecimal("0.80"));
    }

    // =====================================================================
    // SUKŪRIMAS (CREATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai sukuria naują prekę")
    void createPreke_naujaBruksninisKodas_grazinaSukurta() {
        // Arrange
        when(prekeRepository.findByBruksninisKodas("1234567890")).thenReturn(Optional.empty());
        when(prekeRepository.save(any(Preke.class))).thenReturn(pienas);

        Preke nauja = new Preke("Pienas", new BigDecimal("1.50"));
        nauja.setBruksninisKodas("1234567890");

        // Act
        Preke rezultatas = prekeService.createPreke(nauja);

        // Assert
        assertThat(rezultatas).isNotNull();
        assertThat(rezultatas.getPavadinimas()).isEqualTo("Pienas");
        assertThat(rezultatas.getPardavimoKaina()).isEqualByComparingTo("1.50");
        verify(prekeRepository).save(any(Preke.class));
    }

    @Test
    @DisplayName("Neleidžia sukurti prekės su jau egzistuojančiu brūkšniniu kodu")
    void createPreke_egzistuojantisKodas_metoKlaida() {
        // Arrange
        when(prekeRepository.findByBruksninisKodas("1234567890")).thenReturn(Optional.of(pienas));

        Preke nauja = new Preke("Kita prekė", new BigDecimal("2.00"));
        nauja.setBruksninisKodas("1234567890");

        // Act & Assert
        assertThatThrownBy(() -> prekeService.createPreke(nauja))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("brūkšniniu kodu jau egzistuoja");
        verify(prekeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Sukuria prekę be brūkšninio kodo (null)")
    void createPreke_bezBruksninioKodo_sėkmingai() {
        // Arrange
        Preke nauja = new Preke("Vanduo", new BigDecimal("0.80"));
        when(prekeRepository.save(any(Preke.class))).thenReturn(nauja);

        // Act
        Preke rezultatas = prekeService.createPreke(nauja);

        // Assert
        assertThat(rezultatas.getPavadinimas()).isEqualTo("Vanduo");
        verify(prekeRepository).save(any(Preke.class));
    }

    // =====================================================================
    // SKAITYMAS (READ)
    // =====================================================================

    @Test
    @DisplayName("Grąžina visas prekes")
    void getAllPrekes_yraPrekes_grazinaSarasa() {
        // Arrange
        Preke duona = new Preke("Duona", new BigDecimal("2.00"));
        when(prekeRepository.findAll()).thenReturn(Arrays.asList(pienas, duona));

        // Act
        List<Preke> rezultatas = prekeService.getAllPrekes();

        // Assert
        assertThat(rezultatas).hasSize(2);
        assertThat(rezultatas).extracting(Preke::getPavadinimas)
                .containsExactlyInAnyOrder("Pienas", "Duona");
    }

    @Test
    @DisplayName("Randa prekę pagal egzistuojantį ID")
    void getPrekeById_egzistuoja_grazina() {
        // Arrange
        when(prekeRepository.findById(1L)).thenReturn(Optional.of(pienas));

        // Act
        Optional<Preke> rezultatas = prekeService.getPrekeById(1L);

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getPrekesId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Grąžina tuščią, kai prekė neegzistuoja")
    void getPrekeById_neegzistuoja_tuščias() {
        // Arrange
        when(prekeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Preke> rezultatas = prekeService.getPrekeById(99L);

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    @Test
    @DisplayName("Randa prekę pagal brūkšninį kodą")
    void getPrekeByBruksninis_egzistuoja_grazina() {
        // Arrange
        when(prekeRepository.findByBruksninisKodas("1234567890")).thenReturn(Optional.of(pienas));

        // Act
        Optional<Preke> rezultatas = prekeService.getPrekeByBruksninis("1234567890");

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getBruksninisKodas()).isEqualTo("1234567890");
    }

    @Test
    @DisplayName("Grąžina tuščią, kai brūkšninis kodas neegzistuoja")
    void getPrekeByBruksninis_neegzistuoja_tuščias() {
        // Arrange
        when(prekeRepository.findByBruksninisKodas("0000000000")).thenReturn(Optional.empty());

        // Act
        Optional<Preke> rezultatas = prekeService.getPrekeByBruksninis("0000000000");

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    // =====================================================================
    // PAIEŠKA IR FILTRAVIMAS (SEARCH & FILTER)
    // =====================================================================

    @Test
    @DisplayName("Paieška pagal pavadinimą – randa atitinkančias prekes")
    void searchPrekesByPavadinimas_dalinisAtitikimas_grazinaSarasa() {
        // Arrange
        when(prekeRepository.findByPavadinimasContainingIgnoreCase("pia")).thenReturn(Arrays.asList(pienas));

        // Act
        List<Preke> rezultatas = prekeService.searchPrekesByPavadinimas("pia");

        // Assert
        assertThat(rezultatas).hasSize(1);
        assertThat(rezultatas.get(0).getPavadinimas()).isEqualTo("Pienas");
    }

    @Test
    @DisplayName("Paieška pagal pavadinimą – nieko neranda")
    void searchPrekesByPavadinimas_neegzistuoja_tuščiasSarasas() {
        // Arrange
        when(prekeRepository.findByPavadinimasContainingIgnoreCase("xyxyx")).thenReturn(Arrays.asList());

        // Act
        List<Preke> rezultatas = prekeService.searchPrekesByPavadinimas("xyxyx");

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    @Test
    @DisplayName("Filtravimas pagal kategoriją – grąžina prekes")
    void getPrekesByKategorija_egzistuojantiKategorija_grazinaSarasa() {
        // Arrange
        when(prekeRepository.findByKategorijosId(1L)).thenReturn(Arrays.asList(pienas));

        // Act
        List<Preke> rezultatas = prekeService.getPrekesByKategorija(1L);

        // Assert
        assertThat(rezultatas).hasSize(1);
        assertThat(rezultatas.get(0).getKategorijosId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Filtravimas pagal kategoriją – tuščias sąrašas")
    void getPrekesByKategorija_neegzistuojantiKategorija_tuščiasSarasas() {
        // Arrange
        when(prekeRepository.findByKategorijosId(99L)).thenReturn(Arrays.asList());

        // Act
        List<Preke> rezultatas = prekeService.getPrekesByKategorija(99L);

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    // =====================================================================
    // REDAGAVIMAS (UPDATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai atnaujina prekę")
    void updatePreke_egzistuoja_atnaujinaDuomenis() {
        // Arrange
        Preke atnaujinta = new Preke("Pienas 2L", new BigDecimal("2.50"));
        atnaujinta.setBruksninisKodas("9999999999");
        atnaujinta.setMatoVienetas("vnt");

        when(prekeRepository.findById(1L)).thenReturn(Optional.of(pienas));
        when(prekeRepository.findByBruksninisKodas("9999999999")).thenReturn(Optional.empty());
        when(prekeRepository.save(any(Preke.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Preke rezultatas = prekeService.updatePreke(1L, atnaujinta);

        // Assert
        assertThat(rezultatas.getPavadinimas()).isEqualTo("Pienas 2L");
        assertThat(rezultatas.getPardavimoKaina()).isEqualByComparingTo("2.50");
        assertThat(rezultatas.getBruksninisKodas()).isEqualTo("9999999999");
    }

    @Test
    @DisplayName("Klaida atnaujinant neegzistuojančią prekę")
    void updatePreke_neegzistuoja_metoKlaida() {
        // Arrange
        when(prekeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> prekeService.updatePreke(99L, pienas))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nerasta");
    }

    @Test
    @DisplayName("Klaida atnaujinant su kitos prekės brūkšniniu kodu")
    void updatePreke_egzistuojantisKitasBruksninisKodas_metoKlaida() {
        // Arrange
        Preke kita = new Preke("Kita", new BigDecimal("3.00"));
        kita.setPrekesId(2L);
        kita.setBruksninisKodas("DUBLIKATAS");

        when(prekeRepository.findById(1L)).thenReturn(Optional.of(pienas));
        when(prekeRepository.findByBruksninisKodas("DUBLIKATAS")).thenReturn(Optional.of(kita));

        Preke atnaujinta = new Preke("Pienas Updated", new BigDecimal("2.00"));
        atnaujinta.setBruksninisKodas("DUBLIKATAS");

        // Act & Assert
        assertThatThrownBy(() -> prekeService.updatePreke(1L, atnaujinta))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("brūkšniniu kodu jau egzistuoja");
    }

    // =====================================================================
    // TRYNIMAS (DELETE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai ištrina egzistuojančią prekę")
    void deletePreke_egzistuoja_ištrina() {
        // Arrange
        when(prekeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(prekeRepository).deleteById(1L);

        // Act
        prekeService.deletePreke(1L);

        // Assert
        verify(prekeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Klaida trinant neegzistuojančią prekę")
    void deletePreke_neegzistuoja_metoKlaida() {
        // Arrange
        when(prekeRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> prekeService.deletePreke(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nerasta");
        verify(prekeRepository, never()).deleteById(any());
    }
}
