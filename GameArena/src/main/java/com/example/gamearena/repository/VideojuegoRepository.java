package com.example.gamearena.repository;

import com.example.gamearena.model.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {
}
