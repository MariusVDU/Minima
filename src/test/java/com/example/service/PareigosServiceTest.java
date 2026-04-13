package com.example.service;

import com.example.model.Pareigos;
import com.example.repository.PareigosRepository;
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
 * Vienetiniai testai – PareigosService
 * Modelis: Arrange-Act-Assert (AAA)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PareigosService vienetiniai testai")
class PareigosServiceTest {

    @Mock
    private PareigosRepository pareigosRepository;

    @InjectMocks
    private PareigosService pareigosService;

    private Pareigos kasininkas;

    @BeforeEach
    void setUp() {
        kasininkas = new Pareigos("Kasininkas", "Aptarnauja pirkėjus");
        kasininkas.setPareiguId(1L);
    }

    // =====================================================================
    // SUKŪRIMAS (CREATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai sukuria naujas pareigas")
    void createPareigos_naujaPavadinimas_grazinaSukurta() {
        // Arrange
        when(pareigosRepository.existsByPavadinimas("Kasininkas")).thenReturn(false);
        when(pareigosRepository.save(any(Pareigos.class))).thenReturn(kasininkas);

        // Act
        Pareigos rezultatas = pareigosService.createPareigos(new Pareigos("Kasininkas", "Aptarnauja pirkėjus"));

        // Assert
        assertThat(rezultatas).isNotNull();
        assertThat(rezultatas.getPavadinimas()).isEqualTo("Kasininkas");
        verify(pareigosRepository).save(any(Pareigos.class));
    }

    @Test
    @DisplayName("Neleidžia sukurti pareigų su egzistuojančiu pavadinimu")
    void createPareigos_egzistuojantisPavadinimas_metoKlaida() {
        // Arrange
        when(pareigosRepository.existsByPavadinimas("Kasininkas")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> pareigosService.createPareigos(new Pareigos("Kasininkas")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jau egzistuoja");
        verify(pareigosRepository, never()).save(any());
    }

    // =====================================================================
    // SKAITYMAS (READ)
    // =====================================================================

    @Test
    @DisplayName("Grąžina visas pareigas")
    void getAllPareigos_yraPareigos_grazinaSarasa() {
        // Arrange
        Pareigos vadybininkas = new Pareigos("Vadybininkas", "Tvarko prekes");
        vadybininkas.setPareiguId(2L);
        when(pareigosRepository.findAll()).thenReturn(Arrays.asList(kasininkas, vadybininkas));

        // Act
        List<Pareigos> rezultatas = pareigosService.getAllPareigos();

        // Assert
        assertThat(rezultatas).hasSize(2);
        assertThat(rezultatas).extracting(Pareigos::getPavadinimas)
                .containsExactlyInAnyOrder("Kasininkas", "Vadybininkas");
    }

    @Test
    @DisplayName("Randa pareigas pagal egzistuojantį ID")
    void getPareigosById_egzistuoja_grazina() {
        // Arrange
        when(pareigosRepository.findById(1L)).thenReturn(Optional.of(kasininkas));

        // Act
        Optional<Pareigos> rezultatas = pareigosService.getPareigosById(1L);

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getPareiguId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Grąžina tuščią, kai pareigos neegzistuoja pagal ID")
    void getPareigosById_neegzistuoja_tuščias() {
        // Arrange
        when(pareigosRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Pareigos> rezultatas = pareigosService.getPareigosById(99L);

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    @Test
    @DisplayName("Randa pareigas pagal pavadinimą")
    void getPareigasByPavadinimas_egzistuoja_grazina() {
        // Arrange
        when(pareigosRepository.findByPavadinimas("Kasininkas")).thenReturn(Optional.of(kasininkas));

        // Act
        Optional<Pareigos> rezultatas = pareigosService.getPareigasByPavadinimas("Kasininkas");

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getPavadinimas()).isEqualTo("Kasininkas");
    }

    @Test
    @DisplayName("Grąžina tuščią, kai pareigų pavadinimas neegzistuoja")
    void getPareigasByPavadinimas_neegzistuoja_tuščias() {
        // Arrange
        when(pareigosRepository.findByPavadinimas("Direktorius")).thenReturn(Optional.empty());

        // Act
        Optional<Pareigos> rezultatas = pareigosService.getPareigasByPavadinimas("Direktorius");

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    // =====================================================================
    // REDAGAVIMAS (UPDATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai atnaujina pareigas")
    void updatePareigos_egzistuoja_atnaujina() {
        // Arrange
        Pareigos atnaujintos = new Pareigos("Vyresnysis kasininkas", "Atnaujintas aprasymas");
        when(pareigosRepository.findById(1L)).thenReturn(Optional.of(kasininkas));
        when(pareigosRepository.existsByPavadinimas("Vyresnysis kasininkas")).thenReturn(false);
        when(pareigosRepository.save(any(Pareigos.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Pareigos rezultatas = pareigosService.updatePareigos(1L, atnaujintos);

        // Assert
        assertThat(rezultatas.getPavadinimas()).isEqualTo("Vyresnysis kasininkas");
        assertThat(rezultatas.getAprasymas()).isEqualTo("Atnaujintas aprasymas");
    }

    @Test
    @DisplayName("Klaida atnaujinant neegzistuojančias pareigas")
    void updatePareigos_neegzistuoja_metoKlaida() {
        // Arrange
        when(pareigosRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pareigosService.updatePareigos(99L, new Pareigos("Nauja")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nerastos");
    }

    @Test
    @DisplayName("Klaida atnaujinant su kito įrašo pavadinimu")
    void updatePareigos_egzistuojantisPavadinimasKitas_metoKlaida() {
        // Arrange
        when(pareigosRepository.findById(1L)).thenReturn(Optional.of(kasininkas));
        when(pareigosRepository.existsByPavadinimas("Vadybininkas")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> pareigosService.updatePareigos(1L, new Pareigos("Vadybininkas")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jau egzistuoja");
    }

    // =====================================================================
    // TRYNIMAS (DELETE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai ištrina egzistuojančias pareigas")
    void deletePareigos_egzistuoja_ištrina() {
        // Arrange
        when(pareigosRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pareigosRepository).deleteById(1L);

        // Act
        pareigosService.deletePareigos(1L);

        // Assert
        verify(pareigosRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Klaida trinant neegzistuojančias pareigas")
    void deletePareigos_neegzistuoja_metoKlaida() {
        // Arrange
        when(pareigosRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> pareigosService.deletePareigos(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nerastos");
        verify(pareigosRepository, never()).deleteById(any());
    }
}
