package com.example.gamearena.repository;

import com.example.gamearena.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {
}
