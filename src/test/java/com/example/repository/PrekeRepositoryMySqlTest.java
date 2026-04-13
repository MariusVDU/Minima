package com.example.repository;

import com.example.model.Preke;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
@DisplayName("PrekeRepository MySQL integraciniai testai")
class PrekeRepositoryMySqlTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PrekeRepository prekeRepository;

    @Test
    @DisplayName("Randa prekę pagal brūkšninį kodą realioje MySQL DB")
    void findByBruksninisKodas_mysql() {
        Preke preke = new Preke("Pienas", new BigDecimal("1.50"));
        preke.setBruksninisKodas("1234567890");
        preke.setKategorijosId(1L);
        entityManager.persistAndFlush(preke);
        entityManager.clear();

        Optional<Preke> rezultatas = prekeRepository.findByBruksninisKodas("1234567890");

        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getPavadinimas()).isEqualTo("Pienas");
    }

    @Test
    @DisplayName("Randa prekes pagal kategoriją ir dalinį pavadinimą realioje MySQL DB")
    void findByKategorijaAndPavadinimas_mysql() {
        Preke preke = new Preke("Duona", new BigDecimal("2.00"));
        preke.setKategorijosId(5L);
        entityManager.persistAndFlush(preke);
        entityManager.clear();

        assertThat(prekeRepository.findByKategorijosId(5L)).hasSize(1);
        assertThat(prekeRepository.findByPavadinimasContainingIgnoreCase("uon")).hasSize(1);
    }
}