package com.example.repository;

import com.example.model.Kategorija;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("mysql")
@DisplayName("KategorijaRepository MySQL integraciniai testai")
class KategorijaRepositoryMySqlTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private KategorijaRepository kategorijaRepository;

    @Test
    @DisplayName("Išsaugo ir randa kategoriją realioje MySQL DB")
    void saveAndFindByPavadinimas_mysql() {
        Kategorija kategorija = new Kategorija("Elektronika", "Elektronikos prekės");
        entityManager.persistAndFlush(kategorija);
        entityManager.clear();

        Optional<Kategorija> rezultatas = kategorijaRepository.findByPavadinimas("Elektronika");

        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getAprasymas()).isEqualTo("Elektronikos prekės");
    }

    @Test
    @DisplayName("Išsaugo ir randa kategoriją pagal ID realioje MySQL DB")
    void saveAndFindById_mysql() {
        Kategorija kategorija = new Kategorija("Maistas", "Maisto prekės");
        entityManager.persistAndFlush(kategorija);
        entityManager.clear();

        Optional<Kategorija> rezultatas = kategorijaRepository.findById(kategorija.getKategorijosId());

        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getPavadinimas()).isEqualTo("Maistas");
    }
}