package com.example.service;

import com.example.model.Pardavimas;
import com.example.model.PardavimoEilute;
import com.example.repository.PardavimoEiluteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Vienetiniai testai - PardavimoEiluteService
 * Modelis: Arrange-Act-Assert (AAA)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PardavimoEiluteService vienetiniai testai")
class PardavimoEiluteServiceTest {

    @Mock
    private PardavimoEiluteRepository pardavimoEiluteRepository;

    @Mock
    private PardavimasService pardavimasService;

    @Mock
    private InventoriusService inventoriusService;

    @InjectMocks
    private PardavimoEiluteService pardavimoEiluteService;

    private Pardavimas pardavimas;
    private PardavimoEilute eilute;

    @BeforeEach
    void setUp() {
        pardavimas = new Pardavimas(1L, 1L);
        pardavimas.setPardavimoId(1L);

        eilute = new PardavimoEilute(1L, 10L, new BigDecimal("2"), new BigDecimal("3.00"));
        eilute.setEilutesId(1L);
    }

    @Test
    @DisplayName("Sekmingai sukuria eilute, atnaujina inventoriu ir perskaiciuoja suma")
    void createEilute_galiojantysDuomenys_sekmingai() {
        // Arrange
        when(pardavimasService.getPardavimasById(1L)).thenReturn(Optional.of(pardavimas));
        when(pardavimoEiluteRepository.save(any(PardavimoEilute.class))).thenReturn(eilute);

        // Act
        PardavimoEilute rezultatas = pardavimoEiluteService.createEilute(eilute);

        // Assert
        assertThat(rezultatas).isNotNull();
        assertThat(rezultatas.getSuma()).isEqualByComparingTo("6.00");
        verify(inventoriusService).updateKiekisByPrekeAndParduotuve(10L, 1L, new BigDecimal("-2"));
        verify(pardavimasService).recalculateBendraSuma(1L);
    }

    @Test
    @DisplayName("Klaida kuriant eilute, kai pardavimas nerastas")
    void createEilute_pardavimasNerastas_metoKlaida() {
        // Arrange
        when(pardavimasService.getPardavimasById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pardavimoEiluteService.createEilute(eilute))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pardavimas nerastas");

        verify(pardavimoEiluteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Klaida kuriant eilute, kai nepakanka inventoriaus")
    void createEilute_inventoriusKlaida_metoKlaida() {
        // Arrange
        when(pardavimasService.getPardavimasById(1L)).thenReturn(Optional.of(pardavimas));
        doThrow(new RuntimeException("Nepakankamas kiekis inventoriuje"))
                .when(inventoriusService).updateKiekisByPrekeAndParduotuve(10L, 1L, new BigDecimal("-2"));

        // Act & Assert
        assertThatThrownBy(() -> pardavimoEiluteService.createEilute(eilute))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Inventoriaus klaida");
    }

    @Test
    @DisplayName("Sekmingai atnaujina pardavimo eilute")
    void updateEilute_egzistuoja_atnaujina() {
        // Arrange
        PardavimoEilute details = new PardavimoEilute();
        details.setKiekis(new BigDecimal("5"));
        details.setVienetoKaina(new BigDecimal("2.50"));

        when(pardavimoEiluteRepository.findById(1L)).thenReturn(Optional.of(eilute));
        when(pardavimoEiluteRepository.save(any(PardavimoEilute.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        PardavimoEilute rezultatas = pardavimoEiluteService.updateEilute(1L, details);

        // Assert
        assertThat(rezultatas.getKiekis()).isEqualByComparingTo("5");
        assertThat(rezultatas.getVienetoKaina()).isEqualByComparingTo("2.50");
        assertThat(rezultatas.getSuma()).isEqualByComparingTo("12.50");
        verify(pardavimasService).recalculateBendraSuma(1L);
    }

    @Test
    @DisplayName("Klaida atnaujinant neegzistuojancia eilute")
    void updateEilute_neegzistuoja_metoKlaida() {
        // Arrange
        when(pardavimoEiluteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pardavimoEiluteService.updateEilute(99L, new PardavimoEilute()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pardavimo eilutė nerasta");
    }

    @Test
    @DisplayName("Sekmingai istrina eilute, grazina inventoriu ir perskaiciuoja pardavima")
    void deleteEilute_egzistuoja_istrina() {
        // Arrange
        when(pardavimoEiluteRepository.findById(1L)).thenReturn(Optional.of(eilute));
        when(pardavimasService.getPardavimasById(1L)).thenReturn(Optional.of(pardavimas));

        // Act
        pardavimoEiluteService.deleteEilute(1L);

        // Assert
        verify(inventoriusService).updateKiekisByPrekeAndParduotuve(10L, 1L, new BigDecimal("2"));
        verify(pardavimoEiluteRepository).deleteById(1L);
        verify(pardavimasService).recalculateBendraSuma(1L);
    }

    @Test
    @DisplayName("Klaida trinant neegzistuojancia eilute")
    void deleteEilute_neegzistuoja_metoKlaida() {
        // Arrange
        when(pardavimoEiluteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pardavimoEiluteService.deleteEilute(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pardavimo eilutė nerasta");
    }

    @Test
    @DisplayName("Klaida trinant, kai pardavimas nerastas")
    void deleteEilute_pardavimasNerastas_metoKlaida() {
        // Arrange
        when(pardavimoEiluteRepository.findById(1L)).thenReturn(Optional.of(eilute));
        when(pardavimasService.getPardavimasById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pardavimoEiluteService.deleteEilute(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pardavimas nerastas");

        verify(pardavimoEiluteRepository, never()).deleteById(anyLong());
    }
}
