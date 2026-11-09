package com.example.gamearena.controller;

import com.example.gamearena.model.Participacion;
import com.example.gamearena.service.ParticipacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participaciones")
public class ParticipacionController {

    private final ParticipacionService participacionService;

    public ParticipacionController(ParticipacionService participacionService) {
        this.participacionService = participacionService;
    }

    // POST /participaciones?jugadorId=1&torneoId=1 -> inscribir un jugador en un torneo
    // 201 Created, o 400 Bad Request si el jugador o el torneo no existen
    @PostMapping
    public ResponseEntity<Participacion> inscribir(@RequestParam Long jugadorId,
                                                   @RequestParam Long torneoId,
                                                   @RequestBody Participacion participacion) {
        Participacion creada = participacionService.inscribir(jugadorId, torneoId, participacion);
        if (creada == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // GET /participaciones -> listar todas las participaciones
    @GetMapping
    public List<Participacion> listar() {
        return participacionService.listar();
    }
}
