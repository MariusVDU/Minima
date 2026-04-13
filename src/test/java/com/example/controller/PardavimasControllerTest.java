package com.example.controller;

import com.example.model.Pardavimas;
import com.example.service.PardavimasService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PardavimasController.class)
@DisplayName("PardavimasController integraciniai testai")
class PardavimasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PardavimasService pardavimasService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pardavimas pardavimas;

    @BeforeEach
    void setUp() {
        pardavimas = new Pardavimas(1L, 1L);
        pardavimas.setPardavimoId(1L);
        pardavimas.setBusena("apmoketas");
        pardavimas.setBendraSuma(new BigDecimal("20.00"));
        pardavimas.setDataLaikas(LocalDateTime.of(2026, 3, 15, 10, 0));
    }

    @Test
    @DisplayName("GET /api/pardavimai - grazina 200 su sarasu")
    void getAllPardavimai_200() throws Exception {
        when(pardavimasService.getAllPardavimai()).thenReturn(Arrays.asList(pardavimas));

        mockMvc.perform(get("/api/pardavimai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].pardavimoId").value(1));
    }

    @Test
    @DisplayName("GET /api/pardavimai/{id} - neegzistuojantis, 404")
    void getPardavimasById_neegzistuoja_404() throws Exception {
        when(pardavimasService.getPardavimasById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pardavimai/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/pardavimai/period - filtruoja pagal perioda")
    void getPardavimaiByPeriod_200() throws Exception {
        when(pardavimasService.getPardavimaiByPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(pardavimas));

        mockMvc.perform(get("/api/pardavimai/period")
                        .param("start", "2026-03-01T00:00:00")
                        .param("end", "2026-03-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("POST /api/pardavimai - sekmingas sukurimas, 201")
    void createPardavimas_201() throws Exception {
        when(pardavimasService.createPardavimas(any(Pardavimas.class))).thenReturn(pardavimas);

        mockMvc.perform(post("/api/pardavimai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pardavimas)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pardavimoId").value(1));
    }

    @Test
    @DisplayName("POST /api/pardavimai - klaida, 400")
    void createPardavimas_klaida_400() throws Exception {
        when(pardavimasService.createPardavimas(any(Pardavimas.class)))
                .thenThrow(new RuntimeException("Neteisingi duomenys"));

        mockMvc.perform(post("/api/pardavimai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pardavimas)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("POST /api/pardavimai - trūksta parduotuvės ID, 400")
        void createPardavimas_trukstaParduotuvesId_400() throws Exception {
                mockMvc.perform(post("/api/pardavimai")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"darbuotojoId\":1}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(pardavimasService);
        }

        @Test
        @DisplayName("PUT /api/pardavimai/{id} - trūksta darbuotojo ID, 400")
        void updatePardavimas_trukstaDarbuotojoId_400() throws Exception {
                mockMvc.perform(put("/api/pardavimai/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"parduotuvesId\":1}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(pardavimasService);
        }

    @Test
    @DisplayName("PUT /api/pardavimai/{id} - neegzistuojantis, 404")
    void updatePardavimas_neegzistuoja_404() throws Exception {
        when(pardavimasService.updatePardavimas(eq(99L), any(Pardavimas.class)))
                .thenThrow(new RuntimeException("Pardavimas nerastas"));

        mockMvc.perform(put("/api/pardavimai/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pardavimas)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/pardavimai/{id} - sekmingas trynimas, 204")
    void deletePardavimas_204() throws Exception {
        doNothing().when(pardavimasService).deletePardavimas(1L);

        mockMvc.perform(delete("/api/pardavimai/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/pardavimai/{id} - neegzistuojantis, 404")
    void deletePardavimas_neegzistuoja_404() throws Exception {
        doThrow(new RuntimeException("Pardavimas nerastas"))
                .when(pardavimasService).deletePardavimas(99L);

        mockMvc.perform(delete("/api/pardavimai/99"))
                .andExpect(status().isNotFound());
    }
}
