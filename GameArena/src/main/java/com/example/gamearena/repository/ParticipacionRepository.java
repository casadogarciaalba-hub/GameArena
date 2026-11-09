package com.example.gamearena.repository;

import com.example.gamearena.model.Participacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipacionRepository extends JpaRepository<Participacion, Long> {

    // Participaciones de un jugador (los torneos en los que ha participado)
    List<Participacion> findByJugadorId(Long jugadorId);

    // Participaciones de un torneo (sus participantes)
    List<Participacion> findByTorneoId(Long torneoId);
}
