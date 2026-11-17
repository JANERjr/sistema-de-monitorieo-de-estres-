package com.bienestar.estres.repository;

import com.bienestar.estres.entity.EvaluacionPss10;
import com.bienestar.estres.enums.Clasificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionPss10Repository extends JpaRepository<EvaluacionPss10, Long> {

    // Última evaluación de un estudiante
    Optional<EvaluacionPss10> findTopByEstudianteIdEstudianteOrderByFechaRealizacionDesc(Long idEstudiante);

    // Historial de evaluaciones de un estudiante (más reciente primero)
    List<EvaluacionPss10> findByEstudianteIdEstudianteOrderByFechaRealizacionDesc(Long idEstudiante);

    Page<EvaluacionPss10> findByEstudianteIdEstudianteOrderByFechaRealizacionDesc(Long idEstudiante, Pageable pageable);

    // Evaluaciones filtradas por clasificación (ej. ALTO, MODERADO)
    List<EvaluacionPss10> findByClasificacion(Clasificacion clasificacion);

    Page<EvaluacionPss10> findByClasificacion(Clasificacion clasificacion, Pageable pageable);

    // Combinación por clasificación y semestre del estudiante
    Page<EvaluacionPss10> findByClasificacionAndEstudianteSemestre(
            Clasificacion clasificacion,
            Integer semestre,
            Pageable pageable
    );
}