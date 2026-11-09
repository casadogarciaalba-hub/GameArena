package com.example.gamearena.repository;

import com.example.gamearena.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    // Torneos con un estado concreto (ABIERTO, EN_CURSO, FINALIZADO)
    List<Torneo> findByEstado(String estado);

    // Torneos de un videojuego (usa el id de la entidad relacionada Videojuego)
    List<Torneo> findByVideojuegoId(Long videojuegoId);

    // Consulta compuesta: torneos de un videojuego Y con un estado concreto
    List<Torneo> findByVideojuegoIdAndEstado(Long videojuegoId, String estado);
}
