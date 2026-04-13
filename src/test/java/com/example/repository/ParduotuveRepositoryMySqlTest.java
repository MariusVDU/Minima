package com.example.repository;

import com.example.model.Parduotuve;
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
@DisplayName("ParduotuveRepository MySQL integraciniai testai")
class ParduotuveRepositoryMySqlTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ParduotuveRepository parduotuveRepository;

    @Test
    @DisplayName("Randa parduotuvę pagal miestą realioje MySQL DB")
    void findByMiestas_mysql() {
        Parduotuve parduotuve = new Parduotuve("Kaunas", "Laisvės al. 1", "+37060000001", "kaunas@test.lt");
        entityManager.persistAndFlush(parduotuve);
        entityManager.clear();

        assertThat(parduotuveRepository.findByMiestas("Kaunas")).hasSize(1);
    }

    @Test
    @DisplayName("Randa parduotuvę pagal telefoną ir el. paštą realioje MySQL DB")
    void findByTelefonasAndElPastas_mysql() {
        Parduotuve parduotuve = new Parduotuve("Vilnius", "Gedimino pr. 1", "+37060000002", "vilnius@test.lt");
        entityManager.persistAndFlush(parduotuve);
        entityManager.clear();

        Optional<Parduotuve> pagalTelefoną = parduotuveRepository.findByTelefonas("+37060000002");
        Optional<Parduotuve> pagalElPastą = parduotuveRepository.findByElPastas("vilnius@test.lt");

        assertThat(pagalTelefoną).isPresent();
        assertThat(pagalElPastą).isPresent();
    }
}