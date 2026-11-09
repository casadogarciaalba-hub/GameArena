package com.example.gamearena.service;

import com.example.gamearena.model.Participacion;
import com.example.gamearena.model.Torneo;
import com.example.gamearena.model.Videojuego;
import com.example.gamearena.repository.ParticipacionRepository;
import com.example.gamearena.repository.TorneoRepository;
import com.example.gamearena.repository.VideojuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TorneoService {

    private final TorneoRepository torneoRepository;
    private final VideojuegoRepository videojuegoRepository;
    private final ParticipacionRepository participacionRepository;

    public TorneoService(TorneoRepository torneoRepository,
                         VideojuegoRepository videojuegoRepository,
                         ParticipacionRepository participacionRepository) {
        this.torneoRepository = torneoRepository;
        this.videojuegoRepository = videojuegoRepository;
        this.participacionRepository = participacionRepository;
    }

    // Comprueba que el estado es uno de los permitidos
    public boolean esEstadoValido(String estado) {
        return "ABIERTO".equals(estado)
                || "EN_CURSO".equals(estado)
                || "FINALIZADO".equals(estado);
    }

    // CREAR: el torneo se asocia al videojuego indicado (si no existe, devuelve null)
    public Torneo crear(Torneo torneo, Long videojuegoId) {
        Videojuego videojuego = videojuegoRepository.findById(videojuegoId).orElse(null);

        if (videojuego == null) {
            return null;
        }

        torneo.setVideojuego(videojuego);
        return torneoRepository.save(torneo);
    }

    // LISTAR TODOS
    public List<Torneo> listar() {
        return torneoRepository.findAll();
    }

    // BUSCAR POR ID (devuelve null si no existe)
    public Torneo obtener(Long id) {
        return torneoRepository.findById(id).orElse(null);
    }

    // MODIFICAR (el videojuego del torneo no cambia)
    public Torneo actualizar(Long id, Torneo datosNuevos) {
        Torneo torneo = torneoRepository.findById(id).orElse(null);

        if (torneo == null) {
            return null;
        }

        torneo.setNombre(datosNuevos.getNombre());
        torneo.setFecha(datosNuevos.getFecha());
        torneo.setPremio(datosNuevos.getPremio());
        torneo.setEstado(datosNuevos.getEstado());

        return torneoRepository.save(torneo);
    }

    // ELIMINAR: primero borramos sus participaciones y después el torneo
    public boolean eliminar(Long id) {
        if (!torneoRepository.existsById(id)) {
            return false;
        }
        List<Participacion> participaciones = participacionRepository.findByTorneoId(id);
        participacionRepository.deleteAll(participaciones);

        torneoRepository.deleteById(id);
        return true;
    }

    // TORNEOS POR ESTADO
    public List<Torneo> buscarPorEstado(String estado) {
        return torneoRepository.findByEstado(estado);
    }

    // TORNEOS DE UN VIDEOJUEGO
    public List<Torneo> buscarPorVideojuego(Long videojuegoId) {
        return torneoRepository.findByVideojuegoId(videojuegoId);
    }

    // CONSULTA COMPUESTA: torneos de un videojuego con un estado concreto
    public List<Torneo> buscarPorVideojuegoYEstado(Long videojuegoId, String estado) {
        return torneoRepository.findByVideojuegoIdAndEstado(videojuegoId, estado);
    }

    // PARTICIPANTES DE UN TORNEO
    public List<Participacion> participantesDeTorneo(Long id) {
        return participacionRepository.findByTorneoId(id);
    }
}
