package com.example.repository;

import com.example.model.Pardavimas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
@DisplayName("PardavimasRepository MySQL integraciniai testai")
class PardavimasRepositoryMySqlTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PardavimasRepository pardavimasRepository;

    @Test
    @DisplayName("Randa pardavimus pagal parduotuvę ir darbuotoją realioje MySQL DB")
    void findByParduotuvesIdAndDarbuotojoId_mysql() {
        Pardavimas pardavimas = new Pardavimas(1L, 2L);
        pardavimas.setDataLaikas(LocalDateTime.of(2026, 3, 10, 12, 0));
        pardavimas.setBusena("apmoketas");
        entityManager.persistAndFlush(pardavimas);
        entityManager.clear();

        assertThat(pardavimasRepository.findByParduotuvesId(1L)).hasSize(1);
        assertThat(pardavimasRepository.findByDarbuotojoId(2L)).hasSize(1);
    }

    @Test
    @DisplayName("Randa pardavimus pagal būseną ir periodą realioje MySQL DB")
    void findByBusenaAndPeriod_mysql() {
        Pardavimas pardavimas = new Pardavimas(3L, 4L);
        pardavimas.setDataLaikas(LocalDateTime.of(2026, 3, 15, 15, 30));
        pardavimas.setBusena("atsauktas");
        entityManager.persistAndFlush(pardavimas);
        entityManager.clear();

        assertThat(pardavimasRepository.findByBusena("atsauktas")).hasSize(1);
        assertThat(pardavimasRepository.findByDataLaikasBetween(
                LocalDateTime.of(2026, 3, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59))).hasSize(1);
    }
}