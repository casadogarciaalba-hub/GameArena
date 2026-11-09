package com.example.gamearena.controller;

import com.example.gamearena.model.Participacion;
import com.example.gamearena.model.Torneo;
import com.example.gamearena.service.TorneoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/torneos")
public class TorneoController {

    private final TorneoService torneoService;

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    // POST /torneos?videojuegoId=1 -> crear torneo asociado a un videojuego
    // 201 Created, o 400 Bad Request si el estado no es válido o el videojuego no existe
    @PostMapping
    public ResponseEntity<Torneo> crear(@RequestBody Torneo torneo, @RequestParam Long videojuegoId) {
        if (!torneoService.esEstadoValido(torneo.getEstado())) {
            return ResponseEntity.badRequest().build();
        }
        Torneo creado = torneoService.crear(torneo, videojuegoId);
        if (creado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // GET /torneos -> listar torneos
    @GetMapping
    public List<Torneo> listar() {
        return torneoService.listar();
    }

    // GET /torneos/{id} -> buscar torneo por id (404 si no existe)
    @GetMapping("/{id}")
    public ResponseEntity<Torneo> obtener(@PathVariable Long id) {
        Torneo torneo = torneoService.obtener(id);
        if (torneo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(torneo);
    }

    // PUT /torneos/{id} -> modificar torneo (400 si el estado no es válido, 404 si no existe)
    @PutMapping("/{id}")
    public ResponseEntity<Torneo> actualizar(@PathVariable Long id, @RequestBody Torneo torneo) {
        if (!torneoService.esEstadoValido(torneo.getEstado())) {
            return ResponseEntity.badRequest().build();
        }
        Torneo actualizado = torneoService.actualizar(id, torneo);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    // DELETE /torneos/{id} -> eliminar torneo (204 No Content, 404 si no existe)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (torneoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET /torneos/estado/ABIERTO -> torneos por estado
    @GetMapping("/estado/{estado}")
    public List<Torneo> buscarPorEstado(@PathVariable String estado) {
        return torneoService.buscarPorEstado(estado);
    }

    // GET /torneos/videojuego/1 -> torneos de un videojuego
    @GetMapping("/videojuego/{videojuegoId}")
    public List<Torneo> buscarPorVideojuego(@PathVariable Long videojuegoId) {
        return torneoService.buscarPorVideojuego(videojuegoId);
    }

    // GET /torneos/videojuego/1/estado/ABIERTO -> CONSULTA COMPUESTA
    @GetMapping("/videojuego/{videojuegoId}/estado/{estado}")
    public List<Torneo> buscarPorVideojuegoYEstado(@PathVariable Long videojuegoId,
                                                   @PathVariable String estado) {
        return torneoService.buscarPorVideojuegoYEstado(videojuegoId, estado);
    }

    // GET /torneos/1/participantes -> participantes de un torneo
    @GetMapping("/{id}/participantes")
    public List<Participacion> participantes(@PathVariable Long id) {
        return torneoService.participantesDeTorneo(id);
    }
}
