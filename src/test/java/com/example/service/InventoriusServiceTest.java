package com.example.service;

import com.example.model.Inventorius;
import com.example.repository.InventoriusRepository;
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
 * Vienetiniai testai – InventoriusService
 * Modelis: Arrange-Act-Assert (AAA)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventoriusService vienetiniai testai")
class InventoriusServiceTest {

    @Mock
    private InventoriusRepository inventoriusRepository;

    @InjectMocks
    private InventoriusService inventoriusService;

    private Inventorius inventorius;

    @BeforeEach
    void setUp() {
        inventorius = new Inventorius(1L, 1L, 50);
        inventorius.setInventoriausId(1L);
        inventorius.setMinimalusKiekis(10);
    }

    // =====================================================================
    // SUKŪRIMAS (CREATE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai sukuria naują inventoriaus įrašą")
    void createInventorius_nėraEgzistuojančio_grazinaSukurta() {
        // Arrange
        when(inventoriusRepository.findByPrekesIdAndParduotuvesId(1L, 1L)).thenReturn(Optional.empty());
        when(inventoriusRepository.save(any(Inventorius.class))).thenReturn(inventorius);

        // Act
        Inventorius rezultatas = inventoriusService.createInventorius(new Inventorius(1L, 1L, 50));

        // Assert
        assertThat(rezultatas).isNotNull();
        assertThat(rezultatas.getKiekis()).isEqualTo(50);
        verify(inventoriusRepository).save(any(Inventorius.class));
    }

    @Test
    @DisplayName("Neleidžia sukurti dublikatų (ta pati prekė ir parduotuvė)")
    void createInventorius_jauEgzistuoja_metoKlaida() {
        // Arrange
        when(inventoriusRepository.findByPrekesIdAndParduotuvesId(1L, 1L)).thenReturn(Optional.of(inventorius));

        // Act & Assert
        assertThatThrownBy(() -> inventoriusService.createInventorius(new Inventorius(1L, 1L, 30)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jau egzistuoja");
        verify(inventoriusRepository, never()).save(any());
    }

    // =====================================================================
    // SKAITYMAS (READ)
    // =====================================================================

    @Test
    @DisplayName("Grąžina visus inventoriaus įrašus")
    void getAllInventorius_yraIrasai_grazinaSarasa() {
        // Arrange
        Inventorius inv2 = new Inventorius(2L, 1L, 20);
        when(inventoriusRepository.findAll()).thenReturn(Arrays.asList(inventorius, inv2));

        // Act
        List<Inventorius> rezultatas = inventoriusService.getAllInventorius();

        // Assert
        assertThat(rezultatas).hasSize(2);
    }

    @Test
    @DisplayName("Randa inventoriaus įrašą pagal ID")
    void getInventoriusById_egzistuoja_grazina() {
        // Arrange
        when(inventoriusRepository.findById(1L)).thenReturn(Optional.of(inventorius));

        // Act
        Optional<Inventorius> rezultatas = inventoriusService.getInventoriusById(1L);

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getInventoriausId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Grąžina žemo kiekio įrašus")
    void getLowStockItems_kiekisMažesnis_grazinaSarasa() {
        // Arrange
        Inventorius mazasKiekis = new Inventorius(2L, 1L, 5);
        when(inventoriusRepository.findByKiekisLessThanEqual(10)).thenReturn(Arrays.asList(mazasKiekis));

        // Act
        List<Inventorius> rezultatas = inventoriusService.getLowStockItems(10);

        // Assert
        assertThat(rezultatas).hasSize(1);
        assertThat(rezultatas.get(0).getKiekis()).isLessThanOrEqualTo(10);
    }

    @Test
    @DisplayName("Randa inventoriaus įrašą pagal prekę ir parduotuvę")
    void getInventoriusByPrekeAndParduotuve_egzistuoja_grazina() {
        // Arrange
        when(inventoriusRepository.findByPrekesIdAndParduotuvesId(1L, 1L)).thenReturn(Optional.of(inventorius));

        // Act
        Optional<Inventorius> rezultatas = inventoriusService.getInventoriusByPrekeAndParduotuve(1L, 1L);

        // Assert
        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getPrekesId()).isEqualTo(1L);
        assertThat(rezultatas.get().getParduotuvesId()).isEqualTo(1L);
    }

    // =====================================================================
    // KIEKIO ATNAUJINIMAS (UPDATE KIEKIS)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai prideda kiekį prie esamo")
    void updateKiekis_teigiamasPokytis_padidina() {
        // Arrange
        when(inventoriusRepository.findById(1L)).thenReturn(Optional.of(inventorius));
        when(inventoriusRepository.save(any(Inventorius.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Inventorius rezultatas = inventoriusService.updateKiekis(1L, 10);

        // Assert
        assertThat(rezultatas.getKiekis()).isEqualTo(60); // 50 + 10
    }

    @Test
    @DisplayName("Sėkmingai atima kiekį")
    void updateKiekis_neigiamaPokytis_sumažina() {
        // Arrange
        when(inventoriusRepository.findById(1L)).thenReturn(Optional.of(inventorius));
        when(inventoriusRepository.save(any(Inventorius.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Inventorius rezultatas = inventoriusService.updateKiekis(1L, -20);

        // Assert
        assertThat(rezultatas.getKiekis()).isEqualTo(30); // 50 - 20
    }

    @Test
    @DisplayName("Klaida, kai kiekis taptu neigiamas")
    void updateKiekis_perDidelisPokytis_metoKlaida() {
        // Arrange
        when(inventoriusRepository.findById(1L)).thenReturn(Optional.of(inventorius));

        // Act & Assert
        assertThatThrownBy(() -> inventoriusService.updateKiekis(1L, -100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("neigiamas");
    }

    @Test
    @DisplayName("Klaida, kai inventoriaus įrašas nerastas pagal ID")
    void updateKiekis_nerastas_metoKlaida() {
        // Arrange
        when(inventoriusRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> inventoriusService.updateKiekis(99L, 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nerastas");
    }

    // =====================================================================
    // KIEKIO ATNAUJINIMAS PAGAL PREKĘ IR PARDUOTUVĘ
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai atnaujina kiekį pagal prekę ir parduotuvę")
    void updateKiekisByPrekeAndParduotuve_pakankamas_atnaujina() {
        // Arrange
        when(inventoriusRepository.findByPrekesIdAndParduotuvesId(1L, 1L)).thenReturn(Optional.of(inventorius));
        when(inventoriusRepository.save(any(Inventorius.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Inventorius rezultatas = inventoriusService.updateKiekisByPrekeAndParduotuve(1L, 1L, new BigDecimal("-10"));

        // Assert
        assertThat(rezultatas.getKiekis()).isEqualTo(40); // 50 - 10
    }

    @Test
    @DisplayName("Klaida, kai nepakanka kiekio inventoriuje")
    void updateKiekisByPrekeAndParduotuve_nepakankamaKiekis_metoKlaida() {
        // Arrange
        when(inventoriusRepository.findByPrekesIdAndParduotuvesId(1L, 1L)).thenReturn(Optional.of(inventorius));

        // Act & Assert
        assertThatThrownBy(() -> inventoriusService.updateKiekisByPrekeAndParduotuve(1L, 1L, new BigDecimal("-100")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nepakankamas kiekis");
    }

    @Test
    @DisplayName("Klaida, kai inventoriaus įrašas nerastas pagal prekę ir parduotuvę")
    void updateKiekisByPrekeAndParduotuve_įrašasNerastas_metoKlaida() {
        // Arrange
        when(inventoriusRepository.findByPrekesIdAndParduotuvesId(99L, 99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> inventoriusService.updateKiekisByPrekeAndParduotuve(99L, 99L, new BigDecimal("-5")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nerastas");
    }

    // =====================================================================
    // TRYNIMAS (DELETE)
    // =====================================================================

    @Test
    @DisplayName("Sėkmingai ištrina inventoriaus įrašą")
    void deleteInventorius_egzistuoja_ištrina() {
        // Arrange
        when(inventoriusRepository.existsById(1L)).thenReturn(true);
        doNothing().when(inventoriusRepository).deleteById(1L);

        // Act
        inventoriusService.deleteInventorius(1L);

        // Assert
        verify(inventoriusRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Klaida trinant neegzistuojantį inventoriaus įrašą")
    void deleteInventorius_neegzistuoja_metoKlaida() {
        // Arrange
        when(inventoriusRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> inventoriusService.deleteInventorius(99L))
                .isInstanceOf(RuntimeException.class);
        verify(inventoriusRepository, never()).deleteById(any());
    }
}
