package com.bienestar.estres.repository;

import com.bienestar.estres.entity.Seguimiento;
import com.bienestar.estres.enums.Clasificacion;
import com.bienestar.estres.enums.EstadoSeguimiento;
import com.bienestar.estres.enums.SeguimientoRequerido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

    // Seguimientos por estudiante
    List<Seguimiento> findByEstudianteIdEstudianteOrderByFechaDesc(Long idEstudiante);

    Page<Seguimiento> findByEstudianteIdEstudianteOrderByFechaDesc(Long idEstudiante, Pageable pageable);

    // Seguimientos pendientes
    List<Seguimiento> findByEstado(EstadoSeguimiento estado);

    Page<Seguimiento> findByEstado(EstadoSeguimiento estado, Pageable pageable);

    // Seguimientos por requerimiento y estado
    List<Seguimiento> findBySeguimientoRequeridoAndEstado(
            SeguimientoRequerido seguimientoRequerido,
            EstadoSeguimiento estado
    );

    // Casos prioritarios: Clasificación = ALTO, SeguimientoRequerido = OBLIGATORIO, Estado = PENDIENTE
    @Query("""
           SELECT s FROM Seguimiento s
           JOIN s.estudiante e
           WHERE s.seguimientoRequerido = :requerido
           AND s.estado = :estado
           AND EXISTS (
               SELECT ev FROM EvaluacionPss10 ev
               WHERE ev.estudiante = e
               AND ev.clasificacion = :clasificacion
               AND ev.fechaRealizacion = (
                   SELECT MAX(ev2.fechaRealizacion)
                   FROM EvaluacionPss10 ev2
                   WHERE ev2.estudiante = e
               )
           )
           ORDER BY s.fecha DESC
           """)
    Page<Seguimiento> findCasosPrioritarios(
            @Param("clasificacion") Clasificacion clasificacion,
            @Param("requerido") SeguimientoRequerido requerido,
            @Param("estado") EstadoSeguimiento estado,
            Pageable pageable
    );
}