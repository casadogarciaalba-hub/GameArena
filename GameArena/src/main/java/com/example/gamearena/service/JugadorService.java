package com.example.gamearena.service;

import com.example.gamearena.model.Jugador;
import com.example.gamearena.model.Participacion;
import com.example.gamearena.repository.JugadorRepository;
import com.example.gamearena.repository.ParticipacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final ParticipacionRepository participacionRepository;

    // Inyección de dependencias por constructor: Spring nos da los repositorios
    public JugadorService(JugadorRepository jugadorRepository,
                          ParticipacionRepository participacionRepository) {
        this.jugadorRepository = jugadorRepository;
        this.participacionRepository = participacionRepository;
    }

    // CREAR
    public Jugador crear(Jugador jugador) {
        return jugadorRepository.save(jugador);
    }

    // LISTAR TODOS
    public List<Jugador> listar() {
        return jugadorRepository.findAll();
    }

    // BUSCAR POR ID (devuelve null si no existe)
    public Jugador obtener(Long id) {
        return jugadorRepository.findById(id).orElse(null);
    }

    // MODIFICAR
    public Jugador actualizar(Long id, Jugador datosNuevos) {
        Jugador jugador = jugadorRepository.findById(id).orElse(null);

        if (jugador == null) {
            return null;
        }

        jugador.setNick(datosNuevos.getNick());
        jugador.setEmail(datosNuevos.getEmail());
        jugador.setNivel(datosNuevos.getNivel());

        return jugadorRepository.save(jugador);
    }

    // ELIMINAR: primero borramos sus participaciones y después el jugador
    public boolean eliminar(Long id) {
        if (!jugadorRepository.existsById(id)) {
            return false;
        }
        List<Participacion> participaciones = participacionRepository.findByJugadorId(id);
        participacionRepository.deleteAll(participaciones);

        jugadorRepository.deleteById(id);
        return true;
    }

    // PARTICIPACIONES DE UN JUGADOR (torneos en los que ha participado)
    public List<Participacion> participacionesDeJugador(Long id) {
        return participacionRepository.findByJugadorId(id);
    }
}
