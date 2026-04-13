package com.example.repository;

import com.example.model.PardavimoEilute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
@DisplayName("PardavimoEiluteRepository MySQL integraciniai testai")
class PardavimoEiluteRepositoryMySqlTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PardavimoEiluteRepository pardavimoEiluteRepository;

    @Test
    @DisplayName("Randa eilutes pagal pardavimą realioje MySQL DB")
    void findByPardavimoId_mysql() {
        PardavimoEilute eile = new PardavimoEilute(1L, 10L, new BigDecimal("2"), new BigDecimal("3.00"));
        entityManager.persistAndFlush(eile);
        entityManager.clear();

        assertThat(pardavimoEiluteRepository.findByPardavimoId(1L)).hasSize(1);
    }

    @Test
    @DisplayName("Randa eilutes pagal prekę realioje MySQL DB")
    void findByPrekesId_mysql() {
        PardavimoEilute eile = new PardavimoEilute(2L, 11L, new BigDecimal("1"), new BigDecimal("4.50"));
        entityManager.persistAndFlush(eile);
        entityManager.clear();

        assertThat(pardavimoEiluteRepository.findByPrekesId(11L)).hasSize(1);
    }
}