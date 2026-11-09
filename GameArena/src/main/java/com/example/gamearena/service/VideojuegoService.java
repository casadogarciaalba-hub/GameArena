package com.example.gamearena.service;

import com.example.gamearena.model.Videojuego;
import com.example.gamearena.repository.VideojuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideojuegoService {

    private final VideojuegoRepository videojuegoRepository;

    public VideojuegoService(VideojuegoRepository videojuegoRepository) {
        this.videojuegoRepository = videojuegoRepository;
    }

    // CREAR
    public Videojuego crear(Videojuego videojuego) {
        return videojuegoRepository.save(videojuego);
    }

    // LISTAR TODOS
    public List<Videojuego> listar() {
        return videojuegoRepository.findAll();
    }

    // BUSCAR POR ID (devuelve null si no existe)
    public Videojuego obtener(Long id) {
        return videojuegoRepository.findById(id).orElse(null);
    }
}
