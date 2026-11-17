package com.bienestar.estres.controller;

import com.bienestar.estres.entity.EvaluacionPss10;
import com.bienestar.estres.service.EvaluacionPss10Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EvaluacionPss10Controller {

    private final EvaluacionPss10Service evaluacionService;

    // HU1: Registrar una nueva evaluación PSS-10
    @PostMapping
    public ResponseEntity<EvaluacionPss10> registrarEvaluacion(@Valid @RequestBody EvaluacionPss10 evaluacion) {
        EvaluacionPss10 nuevaEvaluacion = evaluacionService.registrarEvaluacion(evaluacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEvaluacion);
    }

    // HU1: Consultar la última evaluación de un estudiante
    @GetMapping("/ultima/{idEstudiante}")
    public ResponseEntity<EvaluacionPss10> obtenerUltimaEvaluacion(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(evaluacionService.obtenerUltimaEvaluacion(idEstudiante));
    }

    // HU1: Consultar el historial de evaluaciones de un estudiante
    @GetMapping("/historial/{idEstudiante}")
    public ResponseEntity<List<EvaluacionPss10>> obtenerHistorialEvaluaciones(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(evaluacionService.obtenerHistorialEvaluaciones(idEstudiante));
    }

    // HU2: Consultar el detalle de una evaluación por su ID
    @GetMapping("/{idEvaluacion}")
    public ResponseEntity<EvaluacionPss10> obtenerEvaluacionPorId(@PathVariable Long idEvaluacion) {
        return ResponseEntity.ok(evaluacionService.obtenerEvaluacionPorId(idEvaluacion));
    }
}