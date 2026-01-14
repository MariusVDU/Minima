package com.example.repository;

import com.example.model.PardavimoEilute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PardavimoEiluteRepository extends JpaRepository<PardavimoEilute, Long> {
    List<PardavimoEilute> findByPardavimoId(Long pardavimoId);
    List<PardavimoEilute> findByPrekesId(Long prekesId);
}
