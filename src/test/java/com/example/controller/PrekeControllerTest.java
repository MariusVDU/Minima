package com.example.controller;

import com.example.model.Preke;
import com.example.service.PrekeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integraciniai testai – PrekeController (Web sluoksnis)
 * Naudojamas @WebMvcTest: tikrinamas tik HTTP sluoksnis, servisas yra imituojamas.
 * Modelis: Arrange-Act-Assert (AAA)
 */
@WebMvcTest(PrekeController.class)
@DisplayName("PrekeController integraciniai testai")
class PrekeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrekeService prekeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Preke pienas;

    @BeforeEach
    void setUp() {
        pienas = new Preke("Pienas", new BigDecimal("1.50"));
        pienas.setPrekesId(1L);
        pienas.setBruksninisKodas("1234567890");
        pienas.setKategorijosId(1L);
        pienas.setMatoVienetas("vnt");
    }

    // =====================================================================
    // GET /api/prekes – visos prekės
    // =====================================================================

    @Test
    @DisplayName("GET /api/prekes – grąžina 200 su prekių sąrašu")
    void getAllPrekes_yraPrekes_200() throws Exception {
        // Arrange
        Preke duona = new Preke("Duona", new BigDecimal("2.00"));
        duona.setPrekesId(2L);
        when(prekeService.getAllPrekes()).thenReturn(Arrays.asList(pienas, duona));

        // Act & Assert
        mockMvc.perform(get("/api/prekes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].pavadinimas").value("Pienas"))
                .andExpect(jsonPath("$[1].pavadinimas").value("Duona"));
    }

    // =====================================================================
    // GET /api/prekes/{id} – viena prekė
    // =====================================================================

    @Test
    @DisplayName("GET /api/prekes/{id} – randa egzistuojančią, grąžina 200")
    void getPrekeById_egzistuoja_200() throws Exception {
        // Arrange
        when(prekeService.getPrekeById(1L)).thenReturn(Optional.of(pienas));

        // Act & Assert
        mockMvc.perform(get("/api/prekes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prekesId").value(1))
                .andExpect(jsonPath("$.pavadinimas").value("Pienas"))
                .andExpect(jsonPath("$.pardavimoKaina").value(1.50));
    }

    @Test
    @DisplayName("GET /api/prekes/{id} – neegzistuojanti, grąžina 404")
    void getPrekeById_neegzistuoja_404() throws Exception {
        // Arrange
        when(prekeService.getPrekeById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/prekes/99"))
                .andExpect(status().isNotFound());
    }

    // =====================================================================
    // GET /api/prekes/bruksninis/{kodas} – paieška pagal brūkšninį kodą
    // =====================================================================

    @Test
    @DisplayName("GET /api/prekes/bruksninis/{kodas} – randa, grąžina 200")
    void getPrekeByBruksninis_egzistuoja_200() throws Exception {
        // Arrange
        when(prekeService.getPrekeByBruksninis("1234567890")).thenReturn(Optional.of(pienas));

        // Act & Assert
        mockMvc.perform(get("/api/prekes/bruksninis/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bruksninisKodas").value("1234567890"));
    }

    @Test
    @DisplayName("GET /api/prekes/bruksninis/{kodas} – neegzistuojantis kodas, grąžina 404")
    void getPrekeByBruksninis_neegzistuoja_404() throws Exception {
        // Arrange
        when(prekeService.getPrekeByBruksninis("0000000000")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/prekes/bruksninis/0000000000"))
                .andExpect(status().isNotFound());
    }

    // =====================================================================
    // GET /api/prekes/search?pavadinimas=... – paieška pagal pavadinimą
    // =====================================================================

    @Test
    @DisplayName("GET /api/prekes/search – randa atitinkančias prekes, grąžina 200")
    void searchPrekes_atitinkančiosPavadinimu_200() throws Exception {
        // Arrange
        when(prekeService.searchPrekesByPavadinimas("pia")).thenReturn(Arrays.asList(pienas));

        // Act & Assert
        mockMvc.perform(get("/api/prekes/search").param("pavadinimas", "pia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].pavadinimas").value("Pienas"));
    }

    @Test
    @DisplayName("GET /api/prekes/search – neranda jokių prekių, grąžina 200 su tuščiu sąrašu")
    void searchPrekes_neatitinka_tuščiasSarasas200() throws Exception {
        // Arrange
        when(prekeService.searchPrekesByPavadinimas("xyzabc")).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/prekes/search").param("pavadinimas", "xyzabc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =====================================================================
    // GET /api/prekes/kategorija/{kategorijosId} – filtravimas
    // =====================================================================

    @Test
    @DisplayName("GET /api/prekes/kategorija/{id} – grąžina prekes kategorijos, 200")
    void getPrekesByKategorija_egzistuoja_200() throws Exception {
        // Arrange
        when(prekeService.getPrekesByKategorija(1L)).thenReturn(Arrays.asList(pienas));

        // Act & Assert
        mockMvc.perform(get("/api/prekes/kategorija/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].kategorijosId").value(1));
    }

    @Test
    @DisplayName("GET /api/prekes/kategorija/{id} – neegzistuojanti kategorija, grąžina 200 su tuščiu sąrašu")
    void getPrekesByKategorija_neegzistuoja_tuščiasSarasas200() throws Exception {
        // Arrange
        when(prekeService.getPrekesByKategorija(99L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/prekes/kategorija/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =====================================================================
    // POST /api/prekes – sukūrimas
    // =====================================================================

    @Test
    @DisplayName("POST /api/prekes – sėkmingas sukūrimas, grąžina 201")
    void createPreke_galiojančiDuomenys_201() throws Exception {
        // Arrange
        Preke nauja = new Preke("Pienas", new BigDecimal("1.50"));
        nauja.setBruksninisKodas("1234567890");
        when(prekeService.createPreke(any(Preke.class))).thenReturn(pienas);

        // Act & Assert
        mockMvc.perform(post("/api/prekes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nauja)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prekesId").value(1))
                .andExpect(jsonPath("$.pavadinimas").value("Pienas"));
    }

    @Test
    @DisplayName("POST /api/prekes – dublikatų brūkšninis kodas, grąžina 400")
    void createPreke_egzistuojantisKodas_400() throws Exception {
        // Arrange
        when(prekeService.createPreke(any(Preke.class)))
                .thenThrow(new IllegalArgumentException("Prekė su tokiu brūkšniniu kodu jau egzistuoja"));

        Preke nauja = new Preke("Kita", new BigDecimal("2.00"));
        nauja.setBruksninisKodas("1234567890");

        // Act & Assert
        mockMvc.perform(post("/api/prekes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nauja)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("POST /api/prekes – trūksta pavadinimo, grąžina 400")
        void createPreke_trukstaPavadinimo_400() throws Exception {
                mockMvc.perform(post("/api/prekes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"pardavimoKaina\":1.50}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(prekeService);
        }

    // =====================================================================
    // PUT /api/prekes/{id} – atnaujinimas
    // =====================================================================

    @Test
    @DisplayName("PUT /api/prekes/{id} – sėkmingas atnaujinimas, grąžina 200")
    void updatePreke_egzistuoja_200() throws Exception {
        // Arrange
        Preke atnaujinta = new Preke("Pienas 2L", new BigDecimal("2.50"));
        atnaujinta.setPrekesId(1L);
        when(prekeService.updatePreke(eq(1L), any(Preke.class))).thenReturn(atnaujinta);

        // Act & Assert
        mockMvc.perform(put("/api/prekes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atnaujinta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pavadinimas").value("Pienas 2L"))
                .andExpect(jsonPath("$.pardavimoKaina").value(2.50));
    }

    @Test
    @DisplayName("PUT /api/prekes/{id} – dublikatų kodas, grąžina 400")
    void updatePreke_dublikatoKodas_400() throws Exception {
        // Arrange
        when(prekeService.updatePreke(eq(1L), any(Preke.class)))
                .thenThrow(new IllegalArgumentException("Prekė su tokiu brūkšniniu kodu jau egzistuoja"));

        // Act & Assert
        mockMvc.perform(put("/api/prekes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pienas)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("PUT /api/prekes/{id} – trūksta pardavimo kainos, grąžina 400")
        void updatePreke_trukstaPardavimoKainos_400() throws Exception {
                mockMvc.perform(put("/api/prekes/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"pavadinimas\":\"Pienas 2L\"}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(prekeService);
        }

    @Test
    @DisplayName("PUT /api/prekes/{id} – neegzistuojanti, grąžina 404")
    void updatePreke_neegzistuoja_404() throws Exception {
        // Arrange
        when(prekeService.updatePreke(eq(99L), any(Preke.class)))
                .thenThrow(new RuntimeException("Prekė nerasta su id: 99"));

        // Act & Assert
        mockMvc.perform(put("/api/prekes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pienas)))
                .andExpect(status().isNotFound());
    }

    // =====================================================================
    // DELETE /api/prekes/{id} – trynimas
    // =====================================================================

    @Test
    @DisplayName("DELETE /api/prekes/{id} – sėkmingas trynimas, grąžina 204")
    void deletePreke_egzistuoja_204() throws Exception {
        // Arrange
        doNothing().when(prekeService).deletePreke(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/prekes/1"))
                .andExpect(status().isNoContent());
        verify(prekeService, times(1)).deletePreke(1L);
    }

    @Test
    @DisplayName("DELETE /api/prekes/{id} – neegzistuojanti, grąžina 404")
    void deletePreke_neegzistuoja_404() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Prekė nerasta su id: 99"))
                .when(prekeService).deletePreke(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/prekes/99"))
                .andExpect(status().isNotFound());
    }
}
