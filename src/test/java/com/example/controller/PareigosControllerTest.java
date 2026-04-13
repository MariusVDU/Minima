package com.example.controller;

import com.example.model.Pareigos;
import com.example.service.PareigosService;
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

@WebMvcTest(PareigosController.class)
@DisplayName("PareigosController integraciniai testai")
class PareigosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PareigosService pareigosService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pareigos pareigos;

    @BeforeEach
    void setUp() {
        pareigos = new Pareigos("Kasininkas", "Dirba su kasa");
        pareigos.setPareiguId(1L);
    }

    @Test
    @DisplayName("GET /api/pareigos - grazina 200 su sarasu")
    void getAllPareigos_200() throws Exception {
        when(pareigosService.getAllPareigos()).thenReturn(Arrays.asList(pareigos));

        mockMvc.perform(get("/api/pareigos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/pareigos/{id} - neegzistuojancios, 404")
    void getPareigosById_neegzistuoja_404() throws Exception {
        when(pareigosService.getPareigosById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pareigos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/pareigos/pavadinimas/{pavadinimas} - randa, 200")
    void getPareigosByPavadinimas_200() throws Exception {
        when(pareigosService.getPareigasByPavadinimas("Kasininkas")).thenReturn(Optional.of(pareigos));

        mockMvc.perform(get("/api/pareigos/pavadinimas/Kasininkas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pareiguId").value(1));
    }

    @Test
    @DisplayName("POST /api/pareigos - sekmingas sukurimas, 201")
    void createPareigos_201() throws Exception {
        when(pareigosService.createPareigos(any(Pareigos.class))).thenReturn(pareigos);

        mockMvc.perform(post("/api/pareigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pareigos)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pareiguId").value(1));
    }

    @Test
    @DisplayName("POST /api/pareigos - dublikatas, 400")
    void createPareigos_dublikatas_400() throws Exception {
        when(pareigosService.createPareigos(any(Pareigos.class)))
                .thenThrow(new IllegalArgumentException("jau egzistuoja"));

        mockMvc.perform(post("/api/pareigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pareigos)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("POST /api/pareigos - trūksta pavadinimo, 400")
        void createPareigos_trukstaPavadinimo_400() throws Exception {
                mockMvc.perform(post("/api/pareigos")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"aprasymas\":\"Be pavadinimo\"}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(pareigosService);
        }

    @Test
    @DisplayName("PUT /api/pareigos/{id} - neegzistuojancios, 400")
    void updatePareigos_neegzistuoja_400() throws Exception {
        when(pareigosService.updatePareigos(eq(99L), any(Pareigos.class)))
                .thenThrow(new IllegalArgumentException("Pareigos nerastos"));

        mockMvc.perform(put("/api/pareigos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pareigos)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("PUT /api/pareigos/{id} - trūksta pavadinimo, 400")
        void updatePareigos_trukstaPavadinimo_400() throws Exception {
                mockMvc.perform(put("/api/pareigos/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"aprasymas\":\"Naujas\"}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(pareigosService);
        }

    @Test
    @DisplayName("DELETE /api/pareigos/{id} - sekmingas trynimas, 204")
    void deletePareigos_204() throws Exception {
        doNothing().when(pareigosService).deletePareigos(1L);

        mockMvc.perform(delete("/api/pareigos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/pareigos/{id} - neegzistuojancios, 404")
    void deletePareigos_neegzistuoja_404() throws Exception {
        doThrow(new IllegalArgumentException("Pareigos nerastos"))
                .when(pareigosService).deletePareigos(99L);

        mockMvc.perform(delete("/api/pareigos/99"))
                .andExpect(status().isNotFound());
    }
}
