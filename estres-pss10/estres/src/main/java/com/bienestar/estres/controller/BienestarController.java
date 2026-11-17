package com.bienestar.estres.controller;

import com.bienestar.estres.entity.Estudiante;
import com.bienestar.estres.entity.Seguimiento;
import com.bienestar.estres.enums.Clasificacion;
import com.bienestar.estres.service.BienestarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bienestar")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BienestarController {

    private final BienestarService bienestarService;

    // HU3: Listar estudiantes en riesgo con filtros opcionales (?nivel=ALTO&semestre=3) y paginación
    @GetMapping("/estudiantes-riesgo")
    public ResponseEntity<Page<Estudiante>> listarEstudiantesEnRiesgo(
            @RequestParam(required = false) Clasificacion nivel,
            @RequestParam(required = false) Integer semestre,
            @PageableDefault(size = 10, sort = "idEstudiante") Pageable pageable) {
        return ResponseEntity.ok(bienestarService.listarEstudiantesEnRiesgo(nivel, semestre, pageable));
    }

    // HU3: Listar todos los estudiantes con paginación
    @GetMapping("/estudiantes")
    public ResponseEntity<Page<Estudiante>> listarTodosLosEstudiantes(Pageable pageable) {
        return ResponseEntity.ok(bienestarService.listarTodosLosEstudiantes(pageable));
    }

    // HU3: Consultar información de un estudiante por ID
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<Estudiante> obtenerEstudiantePorId(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(bienestarService.obtenerEstudiantePorId(idEstudiante));
    }

    // HU3: Registrar un nuevo seguimiento para un estudiante
    @PostMapping("/seguimiento")
    public ResponseEntity<Seguimiento> registrarSeguimiento(@Valid @RequestBody Seguimiento seguimiento) {
        Seguimiento nuevoSeguimiento = bienestarService.registrarSeguimiento(seguimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoSeguimiento);
    }

    // HU3: Listar todos los seguimientos de un estudiante
    @GetMapping("/seguimientos/{idEstudiante}")
    public ResponseEntity<List<Seguimiento>> listarSeguimientosPorEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(bienestarService.listarSeguimientosPorEstudiante(idEstudiante));
    }

    // HU3: Listar casos prioritarios (Clasificación ALTO + Seguimiento OBLIGATORIO + Estado PENDIENTE)
    @GetMapping("/casos-prioritarios")
    public ResponseEntity<Page<Seguimiento>> listarCasosPrioritarios(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bienestarService.listarCasosPrioritarios(pageable));
    }
}