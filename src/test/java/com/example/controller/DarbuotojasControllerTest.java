package com.example.controller;

import com.example.model.Darbuotojas;
import com.example.service.DarbuotojasService;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integraciniai testai – DarbuotojasController (Web sluoksnis)
 * Naudojamas @WebMvcTest: tikrinamas tik HTTP sluoksnis, servisas yra imituojamas.
 * Modelis: Arrange-Act-Assert (AAA)
 */
@WebMvcTest(DarbuotojasController.class)
@DisplayName("DarbuotojasController integraciniai testai")
class DarbuotojasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DarbuotojasService darbuotojasService;

    @Autowired
    private ObjectMapper objectMapper;

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
    // GET /api/darbuotojai – visi darbuotojai
    // =====================================================================

    @Test
    @DisplayName("GET /api/darbuotojai – grąžina 200 su darbuotojų sąrašu")
    void getAllDarbuotojai_yraDarbuotojai_200() throws Exception {
        // Arrange
        Darbuotojas petras = new Darbuotojas("Petras", "Petraitis", "39001010002",
                "+37060000002", "petras@test.lt",
                1L, 1L, LocalDate.now(), new BigDecimal("9.00"));
        petras.setId(2L);
        when(darbuotojasService.getAllDarbuotojai()).thenReturn(Arrays.asList(jonas, petras));

        // Act & Assert
        mockMvc.perform(get("/api/darbuotojai"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].vardas").value("Jonas"))
                .andExpect(jsonPath("$[1].vardas").value("Petras"));
    }

    @Test
    @DisplayName("GET /api/darbuotojai – grąžina 200 su tuščiu sąrašu")
    void getAllDarbuotojai_nėraDarbuotojų_tuščiasSarasas200() throws Exception {
        // Arrange
        when(darbuotojasService.getAllDarbuotojai()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/darbuotojai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =====================================================================
    // GET /api/darbuotojai/{id} – vienas darbuotojas
    // =====================================================================

    @Test
    @DisplayName("GET /api/darbuotojai/{id} – randa egzistuojantį, grąžina 200")
    void getDarbuotojasById_egzistuoja_200() throws Exception {
        // Arrange
        when(darbuotojasService.getDarbuotojasById(1L)).thenReturn(Optional.of(jonas));

        // Act & Assert
        mockMvc.perform(get("/api/darbuotojai/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vardas").value("Jonas"))
                .andExpect(jsonPath("$.pavarde").value("Jonaitis"));
    }

    @Test
    @DisplayName("GET /api/darbuotojai/{id} – neegzistuojantis, grąžina 404")
    void getDarbuotojasById_neegzistuoja_404() throws Exception {
        // Arrange
        when(darbuotojasService.getDarbuotojasById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/darbuotojai/99"))
                .andExpect(status().isNotFound());
    }

    // =====================================================================
    // GET /api/darbuotojai/parduotuve/{parduotuvesId} – filtravimas
    // =====================================================================

    @Test
    @DisplayName("GET /api/darbuotojai/parduotuve/{id} – grąžina parduotuvės darbuotojus, 200")
    void getDarbuotojaiByParduotuve_egzistuoja_200() throws Exception {
        // Arrange
        when(darbuotojasService.getDarbuotojaiByParduotuve(1L)).thenReturn(Arrays.asList(jonas));

        // Act & Assert
        mockMvc.perform(get("/api/darbuotojai/parduotuve/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].parduotuvesId").value(1));
    }

    @Test
    @DisplayName("GET /api/darbuotojai/parduotuve/{id} – neegzistuojanti parduotuvė, grąžina tuščią sąrašą 200")
    void getDarbuotojaiByParduotuve_tuščia_200() throws Exception {
        // Arrange
        when(darbuotojasService.getDarbuotojaiByParduotuve(99L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/darbuotojai/parduotuve/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =====================================================================
    // GET /api/darbuotojai/asmens-kodas/{asmensKodas} – paieška
    // =====================================================================

    @Test
    @DisplayName("GET /api/darbuotojai/asmens-kodas/{kodas} – randa, grąžina 200")
    void getDarbuotojasByAsmensKodas_egzistuoja_200() throws Exception {
        // Arrange
        when(darbuotojasService.getDarbuotojasByAsmensKodas("39001010001")).thenReturn(Optional.of(jonas));

        // Act & Assert
        mockMvc.perform(get("/api/darbuotojai/asmens-kodas/39001010001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asmensKodas").value("39001010001"));
    }

    @Test
    @DisplayName("GET /api/darbuotojai/asmens-kodas/{kodas} – neegzistuojantis, grąžina 404")
    void getDarbuotojasByAsmensKodas_neegzistuoja_404() throws Exception {
        // Arrange
        when(darbuotojasService.getDarbuotojasByAsmensKodas("00000000000")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/darbuotojai/asmens-kodas/00000000000"))
                .andExpect(status().isNotFound());
    }

    // =====================================================================
    // POST /api/darbuotojai – sukūrimas
    // =====================================================================

    @Test
    @DisplayName("POST /api/darbuotojai – sėkmingas sukūrimas, grąžina 201")
    void createDarbuotojas_galiojančiDuomenys_201() throws Exception {
        // Arrange
        when(darbuotojasService.createDarbuotojas(any(Darbuotojas.class))).thenReturn(jonas);

        // Act & Assert
        mockMvc.perform(post("/api/darbuotojai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jonas)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vardas").value("Jonas"))
                .andExpect(jsonPath("$.asmensKodas").value("39001010001"));
    }

    @Test
    @DisplayName("POST /api/darbuotojai – dublikatų asmens kodas, grąžina 400")
    void createDarbuotojas_dublikatoAsmensKodas_400() throws Exception {
        // Arrange
        when(darbuotojasService.createDarbuotojas(any(Darbuotojas.class)))
                .thenThrow(new IllegalArgumentException("Darbuotojas su tokiu asmens kodu jau egzistuoja"));

        // Act & Assert
        mockMvc.perform(post("/api/darbuotojai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jonas)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/darbuotojai – dublikatų telefono numeris, grąžina 400")
    void createDarbuotojas_dublikatoTelefonas_400() throws Exception {
        // Arrange
        when(darbuotojasService.createDarbuotojas(any(Darbuotojas.class)))
                .thenThrow(new IllegalArgumentException("Darbuotojas su tokiu telefono numeriu jau egzistuoja"));

        // Act & Assert
        mockMvc.perform(post("/api/darbuotojai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jonas)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("POST /api/darbuotojai – trūksta asmens kodo, grąžina 400")
        void createDarbuotojas_trukstaAsmensKodo_400() throws Exception {
                mockMvc.perform(post("/api/darbuotojai")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"vardas\":\"Jonas\",\"pavarde\":\"Jonaitis\",\"telefonas\":\"+37060000001\",\"parduotuvesId\":1,\"pareiguId\":1,\"idarbinimoData\":\"2020-01-15\",\"valandinisAtlyginimas\":8.50}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(darbuotojasService);
        }

    // =====================================================================
    // PUT /api/darbuotojai/{id} – atnaujinimas
    // =====================================================================

    @Test
    @DisplayName("PUT /api/darbuotojai/{id} – sėkmingas atnaujinimas, grąžina 200")
    void updateDarbuotojas_egzistuoja_200() throws Exception {
        // Arrange
        Darbuotojas atnaujintas = new Darbuotojas(
                "Jonas", "Atnaujintas", "39001010001",
                "+37060000001", "jonas@test.lt",
                1L, 2L, LocalDate.now(), new BigDecimal("10.00")
        );
        atnaujintas.setId(1L);
        when(darbuotojasService.updateDarbuotojas(eq(1L), any(Darbuotojas.class))).thenReturn(atnaujintas);

        // Act & Assert
        mockMvc.perform(put("/api/darbuotojai/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atnaujintas)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pavarde").value("Atnaujintas"));
    }

    @Test
    @DisplayName("PUT /api/darbuotojai/{id} – dublikatų asmens kodas, grąžina 400")
    void updateDarbuotojas_dublikatoAsmensKodas_400() throws Exception {
        // Arrange
        when(darbuotojasService.updateDarbuotojas(eq(1L), any(Darbuotojas.class)))
                .thenThrow(new IllegalArgumentException("Darbuotojas su tokiu asmens kodu jau egzistuoja"));

        // Act & Assert
        mockMvc.perform(put("/api/darbuotojai/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jonas)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("PUT /api/darbuotojai/{id} – trūksta telefono numerio, grąžina 400")
        void updateDarbuotojas_trukstaTelefonas_400() throws Exception {
                mockMvc.perform(put("/api/darbuotojai/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"vardas\":\"Jonas\",\"pavarde\":\"Jonaitis\",\"asmensKodas\":\"39001010001\",\"parduotuvesId\":1,\"pareiguId\":1,\"idarbinimoData\":\"2020-01-15\",\"valandinisAtlyginimas\":8.50}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(darbuotojasService);
        }

    @Test
    @DisplayName("PUT /api/darbuotojai/{id} – neegzistuojantis, grąžina 400 (IllegalArgumentException)")
    void updateDarbuotojas_neegzistuoja_400() throws Exception {
        // Arrange – DarbuotojasController meta IllegalArgumentException, kuri mapuojama į 400
        when(darbuotojasService.updateDarbuotojas(eq(99L), any(Darbuotojas.class)))
                .thenThrow(new IllegalArgumentException("Darbuotojas nerastas su ID: 99"));

        // Act & Assert
        mockMvc.perform(put("/api/darbuotojai/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jonas)))
                .andExpect(status().isBadRequest());
    }

    // =====================================================================
    // DELETE /api/darbuotojai/{id} – trynimas
    // =====================================================================

    @Test
    @DisplayName("DELETE /api/darbuotojai/{id} – sėkmingas trynimas, grąžina 204")
    void deleteDarbuotojas_egzistuoja_204() throws Exception {
        // Arrange
        doNothing().when(darbuotojasService).deleteDarbuotojas(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/darbuotojai/1"))
                .andExpect(status().isNoContent());
        verify(darbuotojasService, times(1)).deleteDarbuotojas(1L);
    }

    @Test
    @DisplayName("DELETE /api/darbuotojai/{id} – neegzistuojantis, grąžina 404")
    void deleteDarbuotojas_neegzistuoja_404() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Darbuotojas nerastas su ID: 99"))
                .when(darbuotojasService).deleteDarbuotojas(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/darbuotojai/99"))
                .andExpect(status().isNotFound());
    }
}
