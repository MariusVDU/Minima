package com.example.repository;

import com.example.model.Inventorius;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoriusRepository extends JpaRepository<Inventorius, Long> {
    Optional<Inventorius> findByPrekesIdAndParduotuvesId(Long prekesId, Long parduotuvesId);
    List<Inventorius> findByPrekesId(Long prekesId);
    List<Inventorius> findByParduotuvesId(Long parduotuvesId);
    List<Inventorius> findByKiekisLessThanEqual(Integer kiekis);
}
