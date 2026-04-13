package com.example.service;

import com.example.model.Darbuotojas;
import com.example.repository.DarbuotojasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Vienetiniai testai – DarbuotojasService
 * Modelis: Arrange-Act-Assert (AAA)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DarbuotojasService vienetiniai testai")
class DarbuotojasServiceTest {

    @Mock
    private DarbuotojasRepository darbuotojasRepository;

    @InjectMocks
    private DarbuotojasService darbuotojasService;

    private Darbuotojas jonas;

    @BeforeEach
    void setUp() {
        jonas = new Darbuotojas(
                "Jonas", "Jonaitis", "39001010001",
                "+37060000001", "jonas@test.lt",
                1L, 1L, LocalDate.of(2020, 1, 15),
                new BigDecimal("8.50")
        );
        jonas.setId(1L);
    }

    // =====================================================================
    // SUKŪRIMAS (CREATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai sukuria naują darbuotoją")
    void createDarbuotojas_visiDuomenysUnikalus_grazinaSukurta() {
        // Arrange
        when(darbuotojasRepository.existsByAsmensKodas("39001010001")).thenReturn(false);
        when(darbuotojasRepository.existsByTelefonas("+37060000001")).thenReturn(false);
        when(darbuotojasRepository.existsByElPastas("jonas@test.lt")).thenReturn(false);
        when(darbuotojasRepository.save(any(Darbuotojas.class))).thenReturn(jonas);

        // Act
        Darbuotojas rezultatas = darbuotojasService.createDarbuotojas(jonas);

        // Assert
        assertThat(rezultatas).isNotNull();
        assertThat(rezultatas.getVardas()).isEqualTo("Jonas");
        assertThat(rezultatas.getPavarde()).isEqualTo("Jonaitis");
        verify(darbuotojasRepository).save(any(Darbuotojas.class));
    }

    @Test
    @DisplayName("Neleidžia sukurti darbuotojo su egzistuojančiu asmens kodu")
    void createDarbuotojas_egzistuojantisAsmensKodas_metoKlaida() {
        // Arrange
        when(darbuotojasRepository.existsByAsmensKodas("39001010001")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> darbuotojasService.createDarbuotojas(jonas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("asmens kodu jau egzistuoja");
        verify(darbuotojasRepository, never()).save(any());
    }

    @Test
    @DisplayName("Neleidžia sukurti darbuotojo su egzistuojančiu telefono numeriu")
    void createDarbuotojas_egzistuojantisTelefonas_metoKlaida() {
        // Arrange
        when(darbuotojasRepository.existsByAsmensKodas(anyString())).thenReturn(false);
        when(darbuotojasRepository.existsByTelefonas("+37060000001")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> darbuotojasService.createDarbuotojas(jonas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("telefono numeriu jau egzistuoja");
        verify(darbuotojasRepository, never()).save(any());
    }

    @Test
    @DisplayName("Neleidžia sukurti darbuotojo su egzistuojančiu el. paštu")
    void createDarbuotojas_egzistuojantisElPastas_metoKlaida() {
        // Arrange
        when(darbuotojasRepository.existsByAsmensKodas(anyString())).thenReturn(false);
        when(darbuotojasRepository.existsByTelefonas(anyString())).thenReturn(false);
        when(darbuotojasRepository.existsByElPastas("jonas@test.lt")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> darbuotojasService.createDarbuotojas(jonas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("el. paštu jau egzistuoja");
    }

    // =====================================================================
    // SKAITYMAS (READ)
    // =====================================================================

    @Test
    @DisplayName("Grąžina visus darbuotojus")
    void getAllDarbuotojai_yra2Darbuotojai_grazina2() {
        // Arrange
        Darbuotojas petras = new Darbuotojas("Petras", "Petraitis", "39001010002",
                "+37060000002", "petras@test.lt",
                1L, 1L, LocalDate.now(), new BigDecimal("9.00"));
        when(darbuotojasRepository.findAll()).thenReturn(Arrays.asList(jonas, petras));

        // Act
        List<Darbuotojas> rezultatas = darbuotojasService.getAllDarbuotojai();

        // Assert
        assertThat(rezultatas).hasSize(2);
        assertThat(rezultatas).extracting(Darbuotojas::getVardas)
                .containsExactlyInAnyOrder("Jonas", "Petras");
    }

    @Test
    @DisplayName("Randa darbuotoją pagal egzistuojantį ID")
    void getDarbuotojasById_egzistuoja_grazina() {
        // Arrange
        when(darbuotojasRepository.findById(1L)).thenReturn(Optional.of(jonas));

        // Act
        Optional<Darbuotojas> rezultatas = darbuotojasService.getDarbuotojasById(1L);

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Grąžina tuščią, kai darbuotojas neegzistuoja")
    void getDarbuotojasById_neegzistuoja_tuščias() {
        // Arrange
        when(darbuotojasRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Darbuotojas> rezultatas = darbuotojasService.getDarbuotojasById(99L);

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    @Test
    @DisplayName("Grąžina darbuotojus pagal parduotuvę")
    void getDarbuotojaiByParduotuve_egzistuoja_grazinaSarasa() {
        // Arrange
        when(darbuotojasRepository.findByParduotuvesId(1L)).thenReturn(Arrays.asList(jonas));

        // Act
        List<Darbuotojas> rezultatas = darbuotojasService.getDarbuotojaiByParduotuve(1L);

        // Assert
        assertThat(rezultatas).hasSize(1);
        assertThat(rezultatas.get(0).getParduotuvesId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Tuščias sąrašas, kai parduotuvėje nėra darbuotojų")
    void getDarbuotojaiByParduotuve_neegzistuoja_tuščiasSarasas() {
        // Arrange
        when(darbuotojasRepository.findByParduotuvesId(99L)).thenReturn(Arrays.asList());

        // Act
        List<Darbuotojas> rezultatas = darbuotojasService.getDarbuotojaiByParduotuve(99L);

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    @Test
    @DisplayName("Randa darbuotoją pagal asmens kodą")
    void getDarbuotojasByAsmensKodas_egzistuoja_grazina() {
        // Arrange
        when(darbuotojasRepository.findByAsmensKodas("39001010001")).thenReturn(Optional.of(jonas));

        // Act
        Optional<Darbuotojas> rezultatas = darbuotojasService.getDarbuotojasByAsmensKodas("39001010001");

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getAsmensKodas()).isEqualTo("39001010001");
    }

    @Test
    @DisplayName("Grąžina tuščią, kai asmens kodas neegzistuoja")
    void getDarbuotojasByAsmensKodas_neegzistuoja_tuščias() {
        // Arrange
        when(darbuotojasRepository.findByAsmensKodas("00000000000")).thenReturn(Optional.empty());

        // Act
        Optional<Darbuotojas> rezultatas = darbuotojasService.getDarbuotojasByAsmensKodas("00000000000");

        // Assert
        assertThat(rezultatas).isEmpty();
    }

    // =====================================================================
    // REDAGAVIMAS (UPDATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai atnaujina darbuotoją (tų pačių laukų reikšmės)")
    void updateDarbuotojas_keičiaTikVardaPavardeAtlyginima_atnaujina() {
        // Arrange – naudojame tuos pačius asmens kodą, telefoną, el. paštą, kad neveiktų unikalumo tikrinimas
        Darbuotojas atnaujintas = new Darbuotojas(
                "Jonas", "Atnaujintas", "39001010001",
                "+37060000001", "jonas@test.lt",
                1L, 2L, LocalDate.now(), new BigDecimal("10.00")
        );
        when(darbuotojasRepository.findById(1L)).thenReturn(Optional.of(jonas));
        when(darbuotojasRepository.save(any(Darbuotojas.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Darbuotojas rezultatas = darbuotojasService.updateDarbuotojas(1L, atnaujintas);

        // Assert
        assertThat(rezultatas.getPavarde()).isEqualTo("Atnaujintas");
        assertThat(rezultatas.getValandinisAtlyginimas()).isEqualByComparingTo("10.00");
        assertThat(rezultatas.getPareiguId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Klaida atnaujinant neegzistuojantį darbuotoją")
    void updateDarbuotojas_neegzistuoja_metoKlaida() {
        // Arrange
        when(darbuotojasRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> darbuotojasService.updateDarbuotojas(99L, jonas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nerastas");
    }

    @Test
    @DisplayName("Klaida atnaujinant su kito darbuotojo asmens kodu")
    void updateDarbuotojas_egzistuojantisAsmensKodasKitas_metoKlaida() {
        // Arrange
        Darbuotojas atnaujintas = new Darbuotojas(
                "Jonas", "Jonaitis", "39001010099", // kitas asmens kodas
                "+37060000001", "jonas@test.lt",
                1L, 1L, LocalDate.now(), new BigDecimal("8.50")
        );
        when(darbuotojasRepository.findById(1L)).thenReturn(Optional.of(jonas));
        when(darbuotojasRepository.existsByAsmensKodas("39001010099")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> darbuotojasService.updateDarbuotojas(1L, atnaujintas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("asmens kodu jau egzistuoja");
    }

    // =====================================================================
    // TRYNIMAS (DELETE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai ištrina egzistuojantį darbuotoją")
    void deleteDarbuotojas_egzistuoja_ištrina() {
        // Arrange
        when(darbuotojasRepository.existsById(1L)).thenReturn(true);
        doNothing().when(darbuotojasRepository).deleteById(1L);

        // Act
        darbuotojasService.deleteDarbuotojas(1L);

        // Assert
        verify(darbuotojasRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Klaida trinant neegzistuojantį darbuotoją")
    void deleteDarbuotojas_neegzistuoja_metoKlaida() {
        // Arrange
        when(darbuotojasRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> darbuotojasService.deleteDarbuotojas(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nerastas");
        verify(darbuotojasRepository, never()).deleteById(any());
    }
}
