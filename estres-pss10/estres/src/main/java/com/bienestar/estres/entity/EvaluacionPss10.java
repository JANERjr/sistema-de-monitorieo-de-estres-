package com.bienestar.estres.entity;

import com.bienestar.estres.enums.Clasificacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluaciones_pss10")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionPss10 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    @Column(name = "fecha_realizacion", nullable = false)
    private LocalDateTime fechaRealizacion;

    @NotBlank(message = "El corte académico es obligatorio")
    @Column(name = "corte_academico", nullable = false, length = 30)
    private String corteAcademico;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta1", nullable = false)
    private Integer respuesta1;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta2", nullable = false)
    private Integer respuesta2;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta3", nullable = false)
    private Integer respuesta3;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta4", nullable = false)
    private Integer respuesta4;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta5", nullable = false)
    private Integer respuesta5;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta6", nullable = false)
    private Integer respuesta6;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta7", nullable = false)
    private Integer respuesta7;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta8", nullable = false)
    private Integer respuesta8;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta9", nullable = false)
    private Integer respuesta9;

    @NotNull @Min(0) @Max(4)
    @Column(name = "respuesta10", nullable = false)
    private Integer respuesta10;

    @Column(name = "puntaje_total", nullable = false)
    private Integer puntajeTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "clasificacion", nullable = false, length = 15)
    private Clasificacion clasificacion;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @PrePersist
    public void prePersist() {
        if (this.fechaRealizacion == null) {
            this.fechaRealizacion = LocalDateTime.now();
        }
    }
}