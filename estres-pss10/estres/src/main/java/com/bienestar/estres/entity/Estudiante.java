package com.bienestar.estres.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante")
    private Long idEstudiante;

    @NotBlank(message = "El código estudiantil es obligatorio")
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre del estudiante es obligatorio")
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @NotNull(message = "El semestre es obligatorio")
    @Min(value = 1, message = "El semestre mínimo es 1")
    @Max(value = 12, message = "El semestre máximo es 12")
    @Column(name = "semestre", nullable = false)
    private Integer semestre;

    @NotNull
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private Boolean estado = true;

    @JsonIgnore
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EvaluacionPss10> evaluaciones = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Seguimiento> seguimientos = new ArrayList<>();
}