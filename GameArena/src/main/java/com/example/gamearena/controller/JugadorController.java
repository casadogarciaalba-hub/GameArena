package com.example.gamearena.controller;

import com.example.gamearena.model.Jugador;
import com.example.gamearena.model.Participacion;
import com.example.gamearena.service.JugadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    // POST /jugadores -> crear jugador (201 Created)
    @PostMapping
    public ResponseEntity<Jugador> crear(@RequestBody Jugador jugador) {
        Jugador creado = jugadorService.crear(jugador);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // GET /jugadores -> listar jugadores
    @GetMapping
    public List<Jugador> listar() {
        return jugadorService.listar();
    }

    // GET /jugadores/{id} -> buscar jugador por id (404 si no existe)
    @GetMapping("/{id}")
    public ResponseEntity<Jugador> obtener(@PathVariable Long id) {
        Jugador jugador = jugadorService.obtener(id);
        if (jugador == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(jugador);
    }

    // PUT /jugadores/{id} -> modificar jugador (404 si no existe)
    @PutMapping("/{id}")
    public ResponseEntity<Jugador> actualizar(@PathVariable Long id, @RequestBody Jugador jugador) {
        Jugador actualizado = jugadorService.actualizar(id, jugador);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    // DELETE /jugadores/{id} -> eliminar jugador (204 No Content, 404 si no existe)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (jugadorService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET /jugadores/{id}/participaciones -> torneos en los que ha participado el jugador
    @GetMapping("/{id}/participaciones")
    public List<Participacion> participaciones(@PathVariable Long id) {
        return jugadorService.participacionesDeJugador(id);
    }
}
