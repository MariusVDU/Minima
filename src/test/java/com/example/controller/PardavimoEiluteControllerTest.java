package com.example.controller;

import com.example.model.PardavimoEilute;
import com.example.service.PardavimoEiluteService;
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

@WebMvcTest(PardavimoEiluteController.class)
@DisplayName("PardavimoEiluteController integraciniai testai")
class PardavimoEiluteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PardavimoEiluteService pardavimoEiluteService;

    @Autowired
    private ObjectMapper objectMapper;

    private PardavimoEilute eilute;

    @BeforeEach
    void setUp() {
        eilute = new PardavimoEilute(1L, 10L, new BigDecimal("2"), new BigDecimal("3.00"));
        eilute.setEilutesId(1L);
    }

    @Test
    @DisplayName("GET /api/pardavimo-eilutes - grazina 200")
    void getAllEilutes_200() throws Exception {
        when(pardavimoEiluteService.getAllEilutes()).thenReturn(Arrays.asList(eilute));

        mockMvc.perform(get("/api/pardavimo-eilutes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/pardavimo-eilutes/{id} - neegzistuojanti, 404")
    void getEiluteById_neegzistuoja_404() throws Exception {
        when(pardavimoEiluteService.getEiluteById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pardavimo-eilutes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/pardavimo-eilutes/pardavimas/{id} - grazina sarasa")
    void getEilutesByPardavimas_200() throws Exception {
        when(pardavimoEiluteService.getEilutesByPardavimas(1L)).thenReturn(Arrays.asList(eilute));

        mockMvc.perform(get("/api/pardavimo-eilutes/pardavimas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("POST /api/pardavimo-eilutes - sekmingas sukurimas, 201")
    void createEilute_201() throws Exception {
        when(pardavimoEiluteService.createEilute(any(PardavimoEilute.class))).thenReturn(eilute);

        mockMvc.perform(post("/api/pardavimo-eilutes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eilute)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eilutesId").value(1));
    }

    @Test
    @DisplayName("POST /api/pardavimo-eilutes - klaida, 400")
    void createEilute_klaida_400() throws Exception {
        when(pardavimoEiluteService.createEilute(any(PardavimoEilute.class)))
                .thenThrow(new RuntimeException("Inventoriaus klaida"));

        mockMvc.perform(post("/api/pardavimo-eilutes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eilute)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("POST /api/pardavimo-eilutes - trūksta kiekio, 400")
        void createEilute_trukstaKiekio_400() throws Exception {
                mockMvc.perform(post("/api/pardavimo-eilutes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"pardavimoId\":1,\"prekesId\":10,\"vienetoKaina\":3.00}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(pardavimoEiluteService);
        }

    @Test
    @DisplayName("PUT /api/pardavimo-eilutes/{id} - sekmingas atnaujinimas, 200")
    void updateEilute_200() throws Exception {
        when(pardavimoEiluteService.updateEilute(eq(1L), any(PardavimoEilute.class))).thenReturn(eilute);

        mockMvc.perform(put("/api/pardavimo-eilutes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eilute)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/pardavimo-eilutes/{id} - neegzistuojanti, 404")
    void updateEilute_neegzistuoja_404() throws Exception {
        when(pardavimoEiluteService.updateEilute(eq(99L), any(PardavimoEilute.class)))
                .thenThrow(new RuntimeException("Pardavimo eilutė nerasta"));

        mockMvc.perform(put("/api/pardavimo-eilutes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eilute)))
                .andExpect(status().isNotFound());
    }

        @Test
        @DisplayName("PUT /api/pardavimo-eilutes/{id} - trūksta vieneto kainos, 400")
        void updateEilute_trukstaVienetoKainos_400() throws Exception {
                mockMvc.perform(put("/api/pardavimo-eilutes/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"pardavimoId\":1,\"prekesId\":10,\"kiekis\":2}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(pardavimoEiluteService);
        }

    @Test
    @DisplayName("DELETE /api/pardavimo-eilutes/{id} - neegzistuojanti, 404")
    void deleteEilute_neegzistuoja_404() throws Exception {
        doThrow(new RuntimeException("Pardavimo eilutė nerasta"))
                .when(pardavimoEiluteService).deleteEilute(99L);

        mockMvc.perform(delete("/api/pardavimo-eilutes/99"))
                .andExpect(status().isNotFound());
    }
}
