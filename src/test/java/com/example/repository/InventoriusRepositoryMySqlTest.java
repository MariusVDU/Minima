package com.example.repository;

import com.example.model.Inventorius;
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
@DisplayName("InventoriusRepository MySQL integraciniai testai")
class InventoriusRepositoryMySqlTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InventoriusRepository inventoriusRepository;

    @Test
    @DisplayName("Randa inventorių pagal prekę ir parduotuvę realioje MySQL DB")
    void findByPrekesIdAndParduotuvesId_mysql() {
        Inventorius inventorius = new Inventorius(10L, 20L, 7);
        entityManager.persistAndFlush(inventorius);
        entityManager.clear();

        Optional<Inventorius> rezultatas = inventoriusRepository.findByPrekesIdAndParduotuvesId(10L, 20L);

        assertThat(rezultatas).isPresent();
        assertThat(rezultatas.get().getKiekis()).isEqualTo(7);
    }

    @Test
    @DisplayName("Randa žemo kiekio įrašus realioje MySQL DB")
    void findLowStockItems_mysql() {
        Inventorius inventorius = new Inventorius(11L, 21L, 3);
        entityManager.persistAndFlush(inventorius);
        entityManager.clear();

        assertThat(inventoriusRepository.findByKiekisLessThanEqual(5)).hasSize(1);
    }
}