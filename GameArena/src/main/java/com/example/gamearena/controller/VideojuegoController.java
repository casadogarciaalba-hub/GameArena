package com.example.gamearena.controller;

import com.example.gamearena.model.Videojuego;
import com.example.gamearena.service.VideojuegoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videojuegos")
public class VideojuegoController {

    private final VideojuegoService videojuegoService;

    public VideojuegoController(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    // POST /videojuegos -> crear videojuego (201 Created)
    @PostMapping
    public ResponseEntity<Videojuego> crear(@RequestBody Videojuego videojuego) {
        Videojuego creado = videojuegoService.crear(videojuego);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // GET /videojuegos -> listar videojuegos
    @GetMapping
    public List<Videojuego> listar() {
        return videojuegoService.listar();
    }

    // GET /videojuegos/{id} -> buscar videojuego por id (404 si no existe)
    @GetMapping("/{id}")
    public ResponseEntity<Videojuego> obtener(@PathVariable Long id) {
        Videojuego videojuego = videojuegoService.obtener(id);
        if (videojuego == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(videojuego);
    }
}
