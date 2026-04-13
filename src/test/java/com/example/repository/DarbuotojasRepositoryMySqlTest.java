package com.example.repository;

import com.example.model.Darbuotojas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
@DisplayName("DarbuotojasRepository MySQL integraciniai testai")
class DarbuotojasRepositoryMySqlTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DarbuotojasRepository darbuotojasRepository;

    @Test
    @DisplayName("Randa darbuotoją pagal asmens kodą realioje MySQL DB")
    void findByAsmensKodas_mysql() {
        Darbuotojas darbuotojas = new Darbuotojas(
                "Jonas", "Jonaitis", "39001010001",
                "+37060000001", "jonas@test.lt",
                1L, 1L, LocalDate.of(2024, 1, 1),
                new BigDecimal("8.50")
        );
        entityManager.persistAndFlush(darbuotojas);
        entityManager.clear();

        assertThat(darbuotojasRepository.findByAsmensKodas("39001010001")).isPresent();
        assertThat(darbuotojasRepository.existsByTelefonas("+37060000001")).isTrue();
        assertThat(darbuotojasRepository.existsByElPastas("jonas@test.lt")).isTrue();
    }

    @Test
    @DisplayName("Randa darbuotojus pagal parduotuvę ir pareigas realioje MySQL DB")
    void findByParduotuvesIdAndPareiguId_mysql() {
        Darbuotojas darbuotojas = new Darbuotojas(
                "Petras", "Petraitis", "39001010002",
                "+37060000002", "petras@test.lt",
                2L, 3L, LocalDate.of(2024, 2, 1),
                new BigDecimal("9.25")
        );
        entityManager.persistAndFlush(darbuotojas);
        entityManager.clear();

        assertThat(darbuotojasRepository.findByParduotuvesId(2L)).hasSize(1);
        assertThat(darbuotojasRepository.findByPareiguId(3L)).hasSize(1);
        assertThat(darbuotojasRepository.findByVardasAndPavarde("Petras", "Petraitis")).hasSize(1);
    }
}