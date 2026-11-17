package com.bienestar.estres.service;

import com.bienestar.estres.entity.Estudiante;
import com.bienestar.estres.entity.EvaluacionPss10;
import com.bienestar.estres.entity.Seguimiento;
import com.bienestar.estres.enums.Clasificacion;
import com.bienestar.estres.enums.EstadoSeguimiento;
import com.bienestar.estres.enums.SeguimientoRequerido;
import com.bienestar.estres.enums.TipoSeguimiento;
import com.bienestar.estres.exception.RecursoNoEncontradoException;
import com.bienestar.estres.repository.EstudianteRepository;
import com.bienestar.estres.repository.EvaluacionPss10Repository;
import com.bienestar.estres.repository.SeguimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluacionPss10Service {

    private final EvaluacionPss10Repository evaluacionRepository;
    private final EstudianteRepository estudianteRepository;
    private final SeguimientoRepository seguimientoRepository;

    @Transactional
    public EvaluacionPss10 registrarEvaluacion(EvaluacionPss10 evaluacion) {
        if (evaluacion.getEstudiante() == null || evaluacion.getEstudiante().getIdEstudiante() == null) {
            throw new IllegalArgumentException("Debe especificar el idEstudiante en la evaluación");
        }

        Long idEstudiante = evaluacion.getEstudiante().getIdEstudiante();
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + idEstudiante));

        // 1. Cálculo oficial PSS-10 (ítems 4, 5, 7 y 8 invertidos: 0->4, 1->3, 2->2, 3->1, 4->0)
        int p1 = evaluacion.getRespuesta1();
        int p2 = evaluacion.getRespuesta2();
        int p3 = evaluacion.getRespuesta3();
        int p4 = invertirPuntaje(evaluacion.getRespuesta4());
        int p5 = invertirPuntaje(evaluacion.getRespuesta5());
        int p6 = evaluacion.getRespuesta6();
        int p7 = invertirPuntaje(evaluacion.getRespuesta7());
        int p8 = invertirPuntaje(evaluacion.getRespuesta8());
        int p9 = evaluacion.getRespuesta9();
        int p10 = evaluacion.getRespuesta10();

        int puntajeTotal = p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10;

        // 2. Clasificación oficial PSS-10
        Clasificacion clasificacion = clasificarPuntaje(puntajeTotal);

        evaluacion.setEstudiante(estudiante);
        evaluacion.setPuntajeTotal(puntajeTotal);
        evaluacion.setClasificacion(clasificacion);
        if (evaluacion.getFechaRealizacion() == null) {
            evaluacion.setFechaRealizacion(LocalDateTime.now());
        }

        EvaluacionPss10 evaluacionGuardada = evaluacionRepository.save(evaluacion);

        // 3. Si es ALTO, se genera automáticamente Caso Prioritario (OBLIGATORIO + PENDIENTE)
        if (clasificacion == Clasificacion.ALTO) {
            Seguimiento casoPrioritario = Seguimiento.builder()
                    .fecha(LocalDateTime.now())
                    .tipoSeguimiento(TipoSeguimiento.ORIENTACION_ACADEMICA)
                    .observacion("Caso prioritario generado automáticamente por resultado PSS-10 ALTO (" + puntajeTotal + "/40)")
                    .estado(EstadoSeguimiento.PENDIENTE)
                    .seguimientoRequerido(SeguimientoRequerido.OBLIGATORIO)
                    .estudiante(estudiante)
                    .build();
            seguimientoRepository.save(casoPrioritario);
        }

        return evaluacionGuardada;
    }

    @Transactional(readOnly = true)
    public EvaluacionPss10 obtenerUltimaEvaluacion(Long idEstudiante) {
        if (!estudianteRepository.existsById(idEstudiante)) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + idEstudiante);
        }
        return evaluacionRepository.findTopByEstudianteIdEstudianteOrderByFechaRealizacionDesc(idEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("El estudiante con ID " + idEstudiante + " aún no tiene evaluaciones registradas"));
    }

    @Transactional(readOnly = true)
    public List<EvaluacionPss10> obtenerHistorialEvaluaciones(Long idEstudiante) {
        if (!estudianteRepository.existsById(idEstudiante)) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + idEstudiante);
        }
        return evaluacionRepository.findByEstudianteIdEstudianteOrderByFechaRealizacionDesc(idEstudiante);
    }

    @Transactional(readOnly = true)
    public EvaluacionPss10 obtenerEvaluacionPorId(Long idEvaluacion) {
        return evaluacionRepository.findById(idEvaluacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evaluación no encontrada con ID: " + idEvaluacion));
    }

    private int invertirPuntaje(int valor) {
        return 4 - valor;
    }

    public Clasificacion clasificarPuntaje(int puntajeTotal) {
        if (puntajeTotal <= 13) {
            return Clasificacion.BAJO;
        } else if (puntajeTotal <= 26) {
            return Clasificacion.MODERADO;
        } else {
            return Clasificacion.ALTO;
        }
    }
}