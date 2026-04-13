package com.example.service;

import com.example.model.Pardavimas;
import com.example.model.PardavimoEilute;
import com.example.repository.PardavimasRepository;
import com.example.repository.PardavimoEiluteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Vienetiniai testai - PardavimasService
 * Modelis: Arrange-Act-Assert (AAA)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PardavimasService vienetiniai testai")
class PardavimasServiceTest {

    @Mock
    private PardavimasRepository pardavimasRepository;

    @Mock
    private PardavimoEiluteRepository pardavimoEiluteRepository;

    @Mock
    private InventoriusService inventoriusService;

    @InjectMocks
    private PardavimasService pardavimasService;

    private Pardavimas pardavimas;

    @BeforeEach
    void setUp() {
        pardavimas = new Pardavimas(1L, 1L);
        pardavimas.setPardavimoId(1L);
        pardavimas.setBusena("apmoketas");
        pardavimas.setBendraSuma(new BigDecimal("12.50"));
        pardavimas.setDataLaikas(LocalDateTime.of(2026, 3, 1, 10, 30));
    }

    @Test
    @DisplayName("Sukurdamas pardavima nustato numatytasias reiksmes, kai jos nepaduotos")
    void createPardavimas_reiksmesNenurodytos_priskiriaDefault() {
        // Arrange
        Pardavimas naujas = new Pardavimas();
        naujas.setParduotuvesId(1L);
        naujas.setDarbuotojoId(1L);
        naujas.setDataLaikas(null);
        naujas.setBendraSuma(null);

        when(pardavimasRepository.save(any(Pardavimas.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Pardavimas rezultatas = pardavimasService.createPardavimas(naujas);

        // Assert
        assertThat(rezultatas.getDataLaikas()).isNotNull();
        assertThat(rezultatas.getBendraSuma()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(pardavimasRepository).save(naujas);
    }

    @Test
    @DisplayName("Atnaujina pardavimo busena ir bendra suma")
    void updatePardavimas_egzistuoja_atnaujina() {
        // Arrange
        Pardavimas details = new Pardavimas();
        details.setBusena("atsauktas");
        details.setBendraSuma(new BigDecimal("25.99"));

        when(pardavimasRepository.findById(1L)).thenReturn(Optional.of(pardavimas));
        when(pardavimasRepository.save(any(Pardavimas.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Pardavimas rezultatas = pardavimasService.updatePardavimas(1L, details);

        // Assert
        assertThat(rezultatas.getBusena()).isEqualTo("atsauktas");
        assertThat(rezultatas.getBendraSuma()).isEqualByComparingTo("25.99");
    }

    @Test
    @DisplayName("Klaida atnaujinant neegzistuojanti pardavima")
    void updatePardavimas_neegzistuoja_metoKlaida() {
        // Arrange
        when(pardavimasRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pardavimasService.updatePardavimas(99L, new Pardavimas()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pardavimas nerastas");
    }

    @Test
    @DisplayName("Sekmingai istrina pardavima, eilutes ir grazina inventoriu")
    void deletePardavimas_egzistuoja_istrina() {
        // Arrange
        PardavimoEilute eilute = new PardavimoEilute(1L, 10L, new BigDecimal("2"), new BigDecimal("3.00"));

        when(pardavimasRepository.existsById(1L)).thenReturn(true);
        when(pardavimasRepository.findById(1L)).thenReturn(Optional.of(pardavimas));
        when(pardavimoEiluteRepository.findByPardavimoId(1L)).thenReturn(Arrays.asList(eilute));

        // Act
        pardavimasService.deletePardavimas(1L);

        // Assert
        verify(inventoriusService).updateKiekisByPrekeAndParduotuve(10L, 1L, new BigDecimal("2"));
        verify(pardavimoEiluteRepository).deleteAll(anyList());
        verify(pardavimasRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Klaida trinant neegzistuojanti pardavima")
    void deletePardavimas_neegzistuoja_metoKlaida() {
        // Arrange
        when(pardavimasRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> pardavimasService.deletePardavimas(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pardavimas nerastas");

        verify(pardavimasRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Perskaiciuoja bendra suma is pardavimo eiluciu")
    void recalculateBendraSuma_yraEilutes_perskaiciuoja() {
        // Arrange
        PardavimoEilute e1 = new PardavimoEilute(1L, 10L, new BigDecimal("2"), new BigDecimal("3.00"));
        PardavimoEilute e2 = new PardavimoEilute(1L, 11L, new BigDecimal("1"), new BigDecimal("4.50"));

        when(pardavimoEiluteRepository.findByPardavimoId(1L)).thenReturn(Arrays.asList(e1, e2));
        when(pardavimasRepository.findById(1L)).thenReturn(Optional.of(pardavimas));

        // Act
        BigDecimal suma = pardavimasService.recalculateBendraSuma(1L);

        // Assert
        assertThat(suma).isEqualByComparingTo("10.50");
        assertThat(pardavimas.getBendraSuma()).isEqualByComparingTo("10.50");
        verify(pardavimasRepository).save(pardavimas);
    }

    @Test
    @DisplayName("Klaida perskaiciuojant, kai pardavimas nerastas")
    void recalculateBendraSuma_pardavimasNerastas_metoKlaida() {
        // Arrange
        when(pardavimoEiluteRepository.findByPardavimoId(99L)).thenReturn(List.of());
        when(pardavimasRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pardavimasService.recalculateBendraSuma(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pardavimas nerastas");
    }
}
