package com.bienestar.estres.repository;

import com.bienestar.estres.entity.Estudiante;
import com.bienestar.estres.enums.Clasificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Estudiantes por semestre
    List<Estudiante> findBySemestre(Integer semestre);

    Page<Estudiante> findBySemestre(Integer semestre, Pageable pageable);

    Optional<Estudiante> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    // Consulta para HU3: Estudiantes en riesgo (ALTO o MODERADO en su última evaluación), con filtros opcionales y paginación
    @Query("""
           SELECT DISTINCT e FROM Estudiante e
           JOIN e.evaluaciones ev
           WHERE ev.fechaRealizacion = (
               SELECT MAX(ev2.fechaRealizacion)
               FROM EvaluacionPss10 ev2
               WHERE ev2.estudiante = e
           )
           AND ev.clasificacion IN :clasificaciones
           AND (:semestre IS NULL OR e.semestre = :semestre)
           """)
    Page<Estudiante> findEstudiantesEnRiesgo(
            @Param("clasificaciones") List<Clasificacion> clasificaciones,
            @Param("semestre") Integer semestre,
            Pageable pageable
    );
}