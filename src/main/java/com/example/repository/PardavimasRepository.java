package com.example.repository;

import com.example.model.Pardavimas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PardavimasRepository extends JpaRepository<Pardavimas, Long> {
    List<Pardavimas> findByParduotuvesId(Long parduotuvesId);
    List<Pardavimas> findByDarbuotojoId(Long darbuotojoId);
    List<Pardavimas> findByBusena(String busena);
    List<Pardavimas> findByDataLaikasBetween(LocalDateTime start, LocalDateTime end);
    List<Pardavimas> findByParduotuvesIdAndDataLaikasBetween(Long parduotuvesId, LocalDateTime start, LocalDateTime end);
}
