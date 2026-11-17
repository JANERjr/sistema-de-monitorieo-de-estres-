package com.bienestar.estres.entity;

import com.bienestar.estres.enums.EstadoSeguimiento;
import com.bienestar.estres.enums.SeguimientoRequerido;
import com.bienestar.estres.enums.TipoSeguimiento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Long idSeguimiento;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @NotNull(message = "El tipo de seguimiento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_seguimiento", nullable = false, length = 30)
    private TipoSeguimiento tipoSeguimiento;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @NotNull(message = "El estado del seguimiento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSeguimiento estado;

    @NotNull(message = "El nivel de seguimiento requerido es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "seguimiento_requerido", nullable = false, length = 20)
    private SeguimientoRequerido seguimientoRequerido;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }
}