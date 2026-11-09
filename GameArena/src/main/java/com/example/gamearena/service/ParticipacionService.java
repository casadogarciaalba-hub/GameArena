package com.example.gamearena.service;

import com.example.gamearena.model.Jugador;
import com.example.gamearena.model.Participacion;
import com.example.gamearena.model.Torneo;
import com.example.gamearena.repository.JugadorRepository;
import com.example.gamearena.repository.ParticipacionRepository;
import com.example.gamearena.repository.TorneoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipacionService {

    private final ParticipacionRepository participacionRepository;
    private final JugadorRepository jugadorRepository;
    private final TorneoRepository torneoRepository;

    public ParticipacionService(ParticipacionRepository participacionRepository,
                                JugadorRepository jugadorRepository,
                                TorneoRepository torneoRepository) {
        this.participacionRepository = participacionRepository;
        this.jugadorRepository = jugadorRepository;
        this.torneoRepository = torneoRepository;
    }

    // INSCRIBIR un jugador en un torneo
    // Si el jugador o el torneo no existen, devuelve null
    public Participacion inscribir(Long jugadorId, Long torneoId, Participacion participacion) {
        Jugador jugador = jugadorRepository.findById(jugadorId).orElse(null);
        Torneo torneo = torneoRepository.findById(torneoId).orElse(null);

        if (jugador == null || torneo == null) {
            return null;
        }

        participacion.setJugador(jugador);
        participacion.setTorneo(torneo);
        return participacionRepository.save(participacion);
    }

    // LISTAR TODAS
    public List<Participacion> listar() {
        return participacionRepository.findAll();
    }
}
