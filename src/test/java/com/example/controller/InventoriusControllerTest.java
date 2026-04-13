package com.example.controller;

import com.example.model.Inventorius;
import com.example.service.InventoriusService;
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
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integraciniai testai - InventoriusController (Web sluoksnis)
 */
@WebMvcTest(InventoriusController.class)
@DisplayName("InventoriusController integraciniai testai")
class InventoriusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoriusService inventoriusService;

    @Autowired
    private ObjectMapper objectMapper;

    private Inventorius inv;

    @BeforeEach
    void setUp() {
        inv = new Inventorius(1L, 1L, 25);
        inv.setInventoriausId(1L);
        inv.setMinimalusKiekis(10);
    }

    @Test
    @DisplayName("GET /api/inventorius - grazina 200 su sarasu")
    void getAllInventorius_200() throws Exception {
        when(inventoriusService.getAllInventorius()).thenReturn(Arrays.asList(inv));

        mockMvc.perform(get("/api/inventorius"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].inventoriausId").value(1));
    }

    @Test
    @DisplayName("GET /api/inventorius/{id} - egzistuojantis grazina 200")
    void getInventoriusById_egzistuoja_200() throws Exception {
        when(inventoriusService.getInventoriusById(1L)).thenReturn(Optional.of(inv));

        mockMvc.perform(get("/api/inventorius/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prekesId").value(1));
    }

    @Test
    @DisplayName("GET /api/inventorius/{id} - neegzistuojantis grazina 404")
    void getInventoriusById_neegzistuoja_404() throws Exception {
        when(inventoriusService.getInventoriusById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/inventorius/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/inventorius - sekmingas sukurimas, 201")
    void createInventorius_201() throws Exception {
        when(inventoriusService.createInventorius(any(Inventorius.class))).thenReturn(inv);

        mockMvc.perform(post("/api/inventorius")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inv)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.inventoriausId").value(1));
    }

    @Test
    @DisplayName("POST /api/inventorius - dublikatas, 400")
    void createInventorius_dublikatas_400() throws Exception {
        when(inventoriusService.createInventorius(any(Inventorius.class)))
                .thenThrow(new IllegalArgumentException("jau egzistuoja"));

        mockMvc.perform(post("/api/inventorius")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inv)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/inventorius - trūksta prekesId, 400")
    void createInventorius_trukstaPrekesId_400() throws Exception {
        mockMvc.perform(post("/api/inventorius")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"parduotuvesId\":1,\"kiekis\":25}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(inventoriusService);
    }

    @Test
    @DisplayName("PUT /api/inventorius/{id} - trūksta prekesId, 400")
    void updateInventorius_trukstaPrekesId_400() throws Exception {
        mockMvc.perform(put("/api/inventorius/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"parduotuvesId\":1,\"kiekis\":25}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(inventoriusService);
    }

    @Test
    @DisplayName("PATCH /api/inventorius/{id}/kiekis - sekmingas atnaujinimas, 200")
    void updateKiekis_200() throws Exception {
        when(inventoriusService.updateKiekis(1L, 5)).thenReturn(inv);

        mockMvc.perform(patch("/api/inventorius/1/kiekis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("pokytis", 5))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/inventorius/{id}/kiekis - truksta pokycio, 400")
    void updateKiekis_trukstaPokycio_400() throws Exception {
        mockMvc.perform(patch("/api/inventorius/1/kiekis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("kitas", 1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/inventorius/{id} - neegzistuojantis, 404")
    void deleteInventorius_neegzistuoja_404() throws Exception {
        doThrow(new RuntimeException("Inventoriaus įrašas nerastas"))
                .when(inventoriusService).deleteInventorius(99L);

        mockMvc.perform(delete("/api/inventorius/99"))
                .andExpect(status().isNotFound());
    }
}
