package com.example.repository;

import com.example.model.Pareigos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
@DisplayName("PareigosRepository MySQL integraciniai testai")
class PareigosRepositoryMySqlTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PareigosRepository pareigosRepository;

    @Test
    @DisplayName("Randa pareigas pagal pavadinimą realioje MySQL DB")
    void findByPavadinimas_mysql() {
        Pareigos pareigos = new Pareigos("Kasininkas", "Dirba su kasa");
        entityManager.persistAndFlush(pareigos);
        entityManager.clear();

        Optional<Pareigos> rezultatas = pareigosRepository.findByPavadinimas("Kasininkas");

        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getAprasymas()).isEqualTo("Dirba su kasa");
    }

    @Test
    @DisplayName("Tikrina egzistavimą pagal pavadinimą realioje MySQL DB")
    void existsByPavadinimas_mysql() {
        Pareigos pareigos = new Pareigos("Vadybininkas", "Tvarko parduotuvę");
        entityManager.persistAndFlush(pareigos);
        entityManager.clear();

        assertThat(pareigosRepository.existsByPavadinimas("Vadybininkas")).isTrue();
        assertThat(pareigosRepository.existsByPavadinimas("Neegzistuoja")).isFalse();
    }
}