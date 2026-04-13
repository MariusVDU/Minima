package com.example.controller;

import com.example.model.Parduotuve;
import com.example.service.ParduotuveService;
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

@WebMvcTest(ParduotuveController.class)
@DisplayName("ParduotuveController integraciniai testai")
class ParduotuveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParduotuveService parduotuveService;

    @Autowired
    private ObjectMapper objectMapper;

    private Parduotuve parduotuve;

    @BeforeEach
    void setUp() {
        parduotuve = new Parduotuve("Kaunas", "Laisves al. 1", "+37060000001", "kaunas@test.lt");
        parduotuve.setId(1L);
    }

    @Test
    @DisplayName("GET /api/parduotuves - grazina 200 su sarasu")
    void getAllParduotuves_200() throws Exception {
        when(parduotuveService.getAllParduotuves()).thenReturn(Arrays.asList(parduotuve));

        mockMvc.perform(get("/api/parduotuves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/parduotuves/{id} - neegzistuojanti, 404")
    void getParduotuveById_neegzistuoja_404() throws Exception {
        when(parduotuveService.getParduotuveById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/parduotuves/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/parduotuves/miestas/{miestas} - grazina filtruota sarasa")
    void getParduotuvesByMiestas_200() throws Exception {
        when(parduotuveService.getParduotuvesByMiestas("Kaunas")).thenReturn(Arrays.asList(parduotuve));

        mockMvc.perform(get("/api/parduotuves/miestas/Kaunas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("POST /api/parduotuves - sekmingas sukurimas, 201")
    void createParduotuve_201() throws Exception {
        when(parduotuveService.createParduotuve(any(Parduotuve.class))).thenReturn(parduotuve);

        mockMvc.perform(post("/api/parduotuves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parduotuve)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/parduotuves - dublikatas, 400")
    void createParduotuve_dublikatas_400() throws Exception {
        when(parduotuveService.createParduotuve(any(Parduotuve.class)))
                .thenThrow(new IllegalArgumentException("jau egzistuoja"));

        mockMvc.perform(post("/api/parduotuves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parduotuve)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("POST /api/parduotuves - trūksta miesto, 400")
        void createParduotuve_trukstaMiestas_400() throws Exception {
                mockMvc.perform(post("/api/parduotuves")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"gatve\":\"Laisves al. 1\"}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(parduotuveService);
        }

    @Test
    @DisplayName("PUT /api/parduotuves/{id} - sekmingas atnaujinimas, 200")
    void updateParduotuve_200() throws Exception {
        when(parduotuveService.updateParduotuve(eq(1L), any(Parduotuve.class))).thenReturn(parduotuve);

        mockMvc.perform(put("/api/parduotuves/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parduotuve)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/parduotuves/{id} - neegzistuojanti, 400")
    void updateParduotuve_neegzistuoja_400() throws Exception {
        when(parduotuveService.updateParduotuve(eq(99L), any(Parduotuve.class)))
                .thenThrow(new IllegalArgumentException("Parduotuve nerasta"));

        mockMvc.perform(put("/api/parduotuves/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parduotuve)))
                .andExpect(status().isBadRequest());
    }

        @Test
        @DisplayName("PUT /api/parduotuves/{id} - trūksta gatvės, 400")
        void updateParduotuve_trukstaGatve_400() throws Exception {
                mockMvc.perform(put("/api/parduotuves/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"miestas\":\"Kaunas\"}"))
                                .andExpect(status().isBadRequest());
                verifyNoInteractions(parduotuveService);
        }

    @Test
    @DisplayName("DELETE /api/parduotuves/{id} - neegzistuojanti, 404")
    void deleteParduotuve_neegzistuoja_404() throws Exception {
        doThrow(new IllegalArgumentException("Parduotuve nerasta"))
                .when(parduotuveService).deleteParduotuve(99L);

        mockMvc.perform(delete("/api/parduotuves/99"))
                .andExpect(status().isNotFound());
    }
}
