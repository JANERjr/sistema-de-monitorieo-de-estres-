package com.bienestar.estres;

import com.bienestar.estres.entity.Estudiante;
import com.bienestar.estres.entity.EvaluacionPss10;
import com.bienestar.estres.entity.Seguimiento;
import com.bienestar.estres.enums.EstadoSeguimiento;
import com.bienestar.estres.enums.TipoSeguimiento;
import com.bienestar.estres.repository.EstudianteRepository;
import com.bienestar.estres.service.BienestarService;
import com.bienestar.estres.service.EvaluacionPss10Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Random;

@SpringBootApplication
public class EstresApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstresApplication.class, args);
    }

    // Prueba integral y carga de datos en la clase principal (igual que en el modelo PAT CURN)
    @Bean
    CommandLineRunner probarModeloCompleto(
            EstudianteRepository estudianteRepo,
            EvaluacionPss10Service evaluacionService,
            BienestarService bienestarService) {
        return args -> {
            System.out.println("\n==================================================");
            System.out.println("   INICIANDO PRUEBA INTEGRAL PSS-10 BIENESTAR CURN ");
            System.out.println("==================================================\n");

            // -------------------------------------------------------------
            // 1. CREAR ESTUDIANTE PRINCIPAL Y REGISTRAR EVALUACIÓN PSS-10
            // -------------------------------------------------------------
            Estudiante estudiante = new Estudiante();
            estudiante.setCodigo("EST-2026001");
            estudiante.setNombre("Janer Peña");
            estudiante.setSemestre(3);
            estudiante.setEstado(true);

            Estudiante eGuardado = estudianteRepo.save(estudiante);
            System.out.println("[OK] Estudiante registrado con ID: " + eGuardado.getIdEstudiante() + " - " + eGuardado.getNombre());

            // -------------------------------------------------------------
            // 2. REGISTRAR EVALUACIONES PSS-10 (CON INVERSIÓN ÍTEMS 4,5,7,8)
            // -------------------------------------------------------------
            EvaluacionPss10 eval1 = new EvaluacionPss10();
            eval1.setEstudiante(eGuardado);
            eval1.setCorteAcademico("2026-1");
            eval1.setFechaRealizacion(LocalDateTime.now().minusMonths(3));
            eval1.setRespuesta1(2);
            eval1.setRespuesta2(2);
            eval1.setRespuesta3(2);
            eval1.setRespuesta4(2); // Invertida -> 2
            eval1.setRespuesta5(2); // Invertida -> 2
            eval1.setRespuesta6(2);
            eval1.setRespuesta7(2); // Invertida -> 2
            eval1.setRespuesta8(2); // Invertida -> 2
            eval1.setRespuesta9(2);
            eval1.setRespuesta10(2);
            EvaluacionPss10 mAnterior = evaluacionService.registrarEvaluacion(eval1);
            System.out.println("[OK] Evaluación 1 (2026-1) registrada -> Puntaje: " + mAnterior.getPuntajeTotal() + " | Clasificación: " + mAnterior.getClasificacion());

            // Segunda evaluación (corte actual 2026-2) con riesgo ALTO
            EvaluacionPss10 eval2 = new EvaluacionPss10();
            eval2.setEstudiante(eGuardado);
            eval2.setCorteAcademico("2026-2");
            eval2.setFechaRealizacion(LocalDateTime.now());
            eval2.setRespuesta1(4);
            eval2.setRespuesta2(3);
            eval2.setRespuesta3(4);
            eval2.setRespuesta4(0); // Invertida -> 4
            eval2.setRespuesta5(1); // Invertida -> 3
            eval2.setRespuesta6(4);
            eval2.setRespuesta7(0); // Invertida -> 4
            eval2.setRespuesta8(1); // Invertida -> 3
            eval2.setRespuesta9(3);
            eval2.setRespuesta10(4);
            EvaluacionPss10 mActual = evaluacionService.registrarEvaluacion(eval2);

            System.out.println("\n--------------------------------------------------");
            System.out.println("   RESULTADO DE LA EVALUACIÓN PSS-10 EN H2        ");
            System.out.println("--------------------------------------------------");
            System.out.println("ID Evaluación:    " + mActual.getIdEvaluacion());
            System.out.println("Corte Académico:  " + mActual.getCorteAcademico());
            System.out.println("Puntaje Total:    " + mActual.getPuntajeTotal() + " / 40");
            System.out.println("Clasificación:    " + mActual.getClasificacion());
            System.out.println("==================================================\n");

            // -------------------------------------------------------------
            // 3. REGISTRO DE SEGUIMIENTO (BIENESTAR UNIVERSITARIO)
            // -------------------------------------------------------------
            Seguimiento seguimiento = new Seguimiento();
            seguimiento.setEstudiante(eGuardado);
            seguimiento.setTipoSeguimiento(TipoSeguimiento.ORIENTACION_ACADEMICA);
            seguimiento.setObservacion("Remitido a orientación psicológica por estrés académico ALTO.");
            seguimiento.setEstado(EstadoSeguimiento.EN_SEGUIMIENTO);
            seguimiento.setFecha(LocalDateTime.now());
            Seguimiento sGuardado = bienestarService.registrarSeguimiento(seguimiento);
            System.out.println("[OK] Seguimiento registrado con ID: " + sGuardado.getIdSeguimiento() + " | Requerido: " + sGuardado.getSeguimientoRequerido());

            // -------------------------------------------------------------
            // 4. OTROS ESTUDIANTES DE TU LISTA ORIGINAL
            // -------------------------------------------------------------
            Estudiante e2 = new Estudiante();
            e2.setCodigo("EST-2026002");
            e2.setNombre("Luis Martinez");
            e2.setSemestre(2);
            e2.setEstado(true);
            estudianteRepo.save(e2);

            Estudiante e3 = new Estudiante();
            e3.setCodigo("EST-2026003");
            e3.setNombre("María Gomez");
            e3.setSemestre(5);
            e3.setEstado(true);
            estudianteRepo.save(e3);

            Estudiante e4 = new Estudiante();
            e4.setCodigo("EST-2026004");
            e4.setNombre("Carlos Perez");
            e4.setSemestre(4);
            e4.setEstado(true);
            estudianteRepo.save(e4);

            Estudiante e5 = new Estudiante();
            e5.setCodigo("EST-2026005");
            e5.setNombre("Joaquito Muñoz");
            e5.setSemestre(3);
            e5.setEstado(true);
            estudianteRepo.save(e5);
            System.out.println("[OK] 4 estudiantes adicionales registrados (Luis, María, Carlos, Joaquito).");

            // -------------------------------------------------------------
            // 5. GENERAR 100 ESTUDIANTES DE PRUEBA PARA PAGINACIÓN (PAGE / SIZE)
            // -------------------------------------------------------------
            Random random = new Random(42);
            for (int i = 1; i <= 100; i++) {
                Estudiante estPrueba = new Estudiante();
                estPrueba.setCodigo(String.format("EST-PRU-%03d", i));
                estPrueba.setNombre("Estudiante Prueba " + i);
                estPrueba.setSemestre((i % 10) + 1); // Semestres del 1 al 10
                estPrueba.setEstado(true);
                Estudiante estGuardado = estudianteRepo.save(estPrueba);

                // Asignar evaluación PSS-10 a cada uno para probar filtros y paginación en riesgo
                EvaluacionPss10 evalPrueba = new EvaluacionPss10();
                evalPrueba.setEstudiante(estGuardado);
                evalPrueba.setCorteAcademico("2026-2");
                evalPrueba.setFechaRealizacion(LocalDateTime.now().minusDays(random.nextInt(15)));

                // Distribuir entre ALTO (30), MODERADO (45) y BAJO (25)
                int rDir = (i <= 30) ? 4 : (i <= 75) ? 2 : 0;
                int rInv = (i <= 30) ? 0 : (i <= 75) ? 2 : 4;

                evalPrueba.setRespuesta1(rDir);
                evalPrueba.setRespuesta2(rDir);
                evalPrueba.setRespuesta3(rDir);
                evalPrueba.setRespuesta4(rInv);
                evalPrueba.setRespuesta5(rInv);
                evalPrueba.setRespuesta6(rDir);
                evalPrueba.setRespuesta7(rInv);
                evalPrueba.setRespuesta8(rInv);
                evalPrueba.setRespuesta9(rDir);
                evalPrueba.setRespuesta10(rDir);

                evaluacionService.registrarEvaluacion(evalPrueba);
            }
            System.out.println("[OK] 100 estudiantes de prueba generados con evaluaciones PSS-10 para paginación.");
            System.out.println("==================================================\n");
        };
    }
}