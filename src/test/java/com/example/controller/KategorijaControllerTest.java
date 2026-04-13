package com.example.controller;

import com.example.model.Kategorija;
import com.example.service.KategorijaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integraciniai testai – KategorijaController (Web sluoksnis)
 * Naudojamas @WebMvcTest: tikrinamas tik HTTP sluoksnis, servisas yra imituojamas.
 * Modelis: Arrange-Act-Assert (AAA)
 */
@WebMvcTest(KategorijaController.class)
@DisplayName("KategorijaController integraciniai testai")
class KategorijaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KategorijaService kategorijaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Kategorija elektronika;

    @BeforeEach
    void setUp() {
        elektronika = new Kategorija("Elektronika", "Elektronikos prekės");
        elektronika.setKategorijosId(1L);
    }

    // =====================================================================
    // GET /api/kategorijos – visos kategorijos
    // =====================================================================

    @Test
    @DisplayName("GET /api/kategorijos – grąžina 200 su kategorijų sąrašu")
    void getAllKategorijos_yraKategorijos_200() throws Exception {
        // Arrange
        Kategorija maistas = new Kategorija("Maistas", null);
        maistas.setKategorijosId(2L);
        when(kategorijaService.getAllKategorijos()).thenReturn(Arrays.asList(elektronika, maistas));

        // Act & Assert
        mockMvc.perform(get("/api/kategorijos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].pavadinimas").value("Elektronika"))
                .andExpect(jsonPath("$[1].pavadinimas").value("Maistas"));
    }

    @Test
    @DisplayName("GET /api/kategorijos – grąžina 200 su tuščiu sąrašu")
    void getAllKategorijos_nėraKategorijų_tuščiasSarasas200() throws Exception {
        // Arrange
        when(kategorijaService.getAllKategorijos()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/kategorijos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =====================================================================
    // GET /api/kategorijos/{id} – viena kategorija
    // =====================================================================

    @Test
    @DisplayName("GET /api/kategorijos/{id} – randa egzistuojančią, grąžina 200")
    void getKategorijaById_egzistuoja_200() throws Exception {
        // Arrange
        when(kategorijaService.getKategorijaById(1L)).thenReturn(Optional.of(elektronika));

        // Act & Assert
        mockMvc.perform(get("/api/kategorijos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kategorijosId").value(1))
                .andExpect(jsonPath("$.pavadinimas").value("Elektronika"));
    }

    @Test
    @DisplayName("GET /api/kategorijos/{id} – neegzistuojanti, grąžina 404")
    void getKategorijaById_neegzistuoja_404() throws Exception {
        // Arrange
        when(kategorijaService.getKategorijaById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/kategorijos/99"))
                .andExpect(status().isNotFound());
    }

    // =====================================================================
    // GET /api/kategorijos/pavadinimas/{pavadinimas} – paieška
    // =====================================================================

    @Test
    @DisplayName("GET /api/kategorijos/pavadinimas/{pavadinimas} – randa, grąžina 200")
    void getKategorijaByPavadinimas_egzistuoja_200() throws Exception {
        // Arrange
        when(kategorijaService.getKategorijaByPavadinimas("Elektronika")).thenReturn(Optional.of(elektronika));

        // Act & Assert
        mockMvc.perform(get("/api/kategorijos/pavadinimas/Elektronika"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pavadinimas").value("Elektronika"));
    }

    @Test
    @DisplayName("GET /api/kategorijos/pavadinimas/{pavadinimas} – neranda, grąžina 404")
    void getKategorijaByPavadinimas_neegzistuoja_404() throws Exception {
        // Arrange
        when(kategorijaService.getKategorijaByPavadinimas("Neegzistuoja")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/kategorijos/pavadinimas/Neegzistuoja"))
                .andExpect(status().isNotFound());
    }

    // =====================================================================
    // POST /api/kategorijos – sukūrimas
    // =====================================================================

    @Test
    @DisplayName("POST /api/kategorijos – sėkmingas sukūrimas, grąžina 201")
    void createKategorija_galiojančiDuomenys_201() throws Exception {
        // Arrange
        Kategorija nauja = new Kategorija("Elektronika", "Elektronikos prekės");
        when(kategorijaService.createKategorija(any(Kategorija.class))).thenReturn(elektronika);

        // Act & Assert
        mockMvc.perform(post("/api/kategorijos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nauja)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kategorijosId").value(1))
                .andExpect(jsonPath("$.pavadinimas").value("Elektronika"));
    }

    @Test
    @DisplayName("POST /api/kategorijos – dublikatų pavadinimas, grąžina 400")
    void createKategorija_dublikatosPavadinimas_400() throws Exception {
        // Arrange
        when(kategorijaService.createKategorija(any(Kategorija.class)))
                .thenThrow(new IllegalArgumentException("Kategorija su tokiu pavadinimu jau egzistuoja"));

        // Act & Assert
        mockMvc.perform(post("/api/kategorijos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Kategorija("Elektronika"))))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("POST /api/kategorijos – trūksta pavadinimo, grąžina 400")
        void createKategorija_trukstaPavadinimo_400() throws Exception {
                mockMvc.perform(post("/api/kategorijos")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"aprasymas\":\"Be pavadinimo\"}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(kategorijaService);
        }

    // =====================================================================
    // PUT /api/kategorijos/{id} – atnaujinimas
    // =====================================================================

    @Test
    @DisplayName("PUT /api/kategorijos/{id} – sėkmingas atnaujinimas, grąžina 200")
    void updateKategorija_egzistuoja_200() throws Exception {
        // Arrange
        Kategorija atnaujinta = new Kategorija("Atnaujinta", "Naujas aprasymas");
        atnaujinta.setKategorijosId(1L);
        when(kategorijaService.updateKategorija(eq(1L), any(Kategorija.class))).thenReturn(atnaujinta);

        // Act & Assert
        mockMvc.perform(put("/api/kategorijos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atnaujinta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pavadinimas").value("Atnaujinta"));
    }

    @Test
    @DisplayName("PUT /api/kategorijos/{id} – dublikatų pavadinimas, grąžina 400")
    void updateKategorija_dublikatosPavadinimas_400() throws Exception {
        // Arrange
        when(kategorijaService.updateKategorija(eq(1L), any(Kategorija.class)))
                .thenThrow(new IllegalArgumentException("Kategorija su tokiu pavadinimu jau egzistuoja"));

        // Act & Assert
        mockMvc.perform(put("/api/kategorijos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Kategorija("Maistas"))))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("PUT /api/kategorijos/{id} – trūksta pavadinimo, grąžina 400")
        void updateKategorija_trukstaPavadinimo_400() throws Exception {
                mockMvc.perform(put("/api/kategorijos/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"aprasymas\":\"Naujas aprasymas\"}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(kategorijaService);
        }

    @Test
    @DisplayName("PUT /api/kategorijos/{id} – neegzistuojanti, grąžina 404")
    void updateKategorija_neegzistuoja_404() throws Exception {
        // Arrange
        when(kategorijaService.updateKategorija(eq(99L), any(Kategorija.class)))
                .thenThrow(new RuntimeException("Kategorija nerasta su id: 99"));

        // Act & Assert
        mockMvc.perform(put("/api/kategorijos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Kategorija("Test"))))
                .andExpect(status().isNotFound());
    }

    // =====================================================================
    // DELETE /api/kategorijos/{id} – trynimas
    // =====================================================================

    @Test
    @DisplayName("DELETE /api/kategorijos/{id} – sėkmingas trynimas, grąžina 204")
    void deleteKategorija_egzistuoja_204() throws Exception {
        // Arrange
        doNothing().when(kategorijaService).deleteKategorija(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/kategorijos/1"))
                .andExpect(status().isNoContent());
        verify(kategorijaService, times(1)).deleteKategorija(1L);
    }

    @Test
    @DisplayName("DELETE /api/kategorijos/{id} – neegzistuojanti, grąžina 404")
    void deleteKategorija_neegzistuoja_404() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Kategorija nerasta su id: 99"))
                .when(kategorijaService).deleteKategorija(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/kategorijos/99"))
                .andExpect(status().isNotFound());
    }
}
