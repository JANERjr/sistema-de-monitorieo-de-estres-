package com.bienestar.estres.service;

import com.bienestar.estres.entity.Estudiante;
import com.bienestar.estres.entity.EvaluacionPss10;
import com.bienestar.estres.entity.Seguimiento;
import com.bienestar.estres.enums.Clasificacion;
import com.bienestar.estres.enums.EstadoSeguimiento;
import com.bienestar.estres.enums.SeguimientoRequerido;
import com.bienestar.estres.exception.RecursoNoEncontradoException;
import com.bienestar.estres.repository.EstudianteRepository;
import com.bienestar.estres.repository.EvaluacionPss10Repository;
import com.bienestar.estres.repository.SeguimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BienestarService {

    private final EstudianteRepository estudianteRepository;
    private final EvaluacionPss10Repository evaluacionRepository;
    private final SeguimientoRepository seguimientoRepository;

    

    @Transactional(readOnly = true)
    public Page<Estudiante> listarEstudiantesEnRiesgo(Clasificacion nivel, Integer semestre, Pageable pageable) {
        List<Clasificacion> clasificacionesFiltro;

        if (nivel != null) {
            clasificacionesFiltro = List.of(nivel);
        } else {
            // Por defecto, estudiantes en riesgo son los clasificados en ALTO o MODERADO
            clasificacionesFiltro = List.of(Clasificacion.ALTO, Clasificacion.MODERADO);
        }

        return estudianteRepository.findEstudiantesEnRiesgo(clasificacionesFiltro, semestre, pageable);
    }

    @Transactional(readOnly = true)
    public Estudiante obtenerEstudiantePorId(Long idEstudiante) {
        return estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + idEstudiante));
    }

    @Transactional
    public Seguimiento registrarSeguimiento(Seguimiento seguimiento) {
        if (seguimiento.getEstudiante() == null || seguimiento.getEstudiante().getIdEstudiante() == null) {
            throw new IllegalArgumentException("Debe especificar el idEstudiante para registrar el seguimiento");
        }

        Long idEstudiante = seguimiento.getEstudiante().getIdEstudiante();
        Estudiante estudiante = obtenerEstudiantePorId(idEstudiante);

        // Determinar SeguimientoRequerido según la última evaluación del estudiante
        SeguimientoRequerido requerido = evaluacionRepository
                .findTopByEstudianteIdEstudianteOrderByFechaRealizacionDesc(idEstudiante)
                .map(EvaluacionPss10::getClasificacion)
                .map(this::determinarSeguimientoRequerido)
                .orElse(SeguimientoRequerido.OPCIONAL);

        seguimiento.setEstudiante(estudiante);
        seguimiento.setSeguimientoRequerido(requerido);

        if (seguimiento.getEstado() == null) {
            seguimiento.setEstado(EstadoSeguimiento.PENDIENTE);
        }
        if (seguimiento.getFecha() == null) {
            seguimiento.setFecha(LocalDateTime.now());
        }

        return seguimientoRepository.save(seguimiento);
    }

    @Transactional(readOnly = true)
    public List<Seguimiento> listarSeguimientosPorEstudiante(Long idEstudiante) {
        if (!estudianteRepository.existsById(idEstudiante)) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + idEstudiante);
        }
        return seguimientoRepository.findByEstudianteIdEstudianteOrderByFechaDesc(idEstudiante);
    }

    @Transactional(readOnly = true)
    public Page<Seguimiento> listarCasosPrioritarios(Pageable pageable) {
        return seguimientoRepository.findCasosPrioritarios(
                Clasificacion.ALTO,
                SeguimientoRequerido.OBLIGATORIO,
                EstadoSeguimiento.PENDIENTE,
                pageable
        );
    }

    public SeguimientoRequerido determinarSeguimientoRequerido(Clasificacion clasificacion) {
        return switch (clasificacion) {
            case BAJO -> SeguimientoRequerido.NO_REQUERIDO;
            case MODERADO -> SeguimientoRequerido.OPCIONAL;
            case ALTO -> SeguimientoRequerido.OBLIGATORIO;
        };
    }

    //listar a todos los estudiantes con paginación
    public Page<Estudiante> listarTodosLosEstudiantes(Pageable pageable) {
        return estudianteRepository.findAll(pageable);
    }
}