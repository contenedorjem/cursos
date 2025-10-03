package es.jemwsg.cursos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.jemwsg.cursos.dto.AlumnoDTOs.AlumnoDTO;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

// DTOs de CURSO.
// - Usamos records para que sean inmutables y concisos.
// - @JsonFormat en fechas para usar formato español (dd/MM/yyyy).
// - Anotaciones de validación en los DTOs de entrada (crear/actualizar).
public class CursoDTOs {
        // Lo que devolvemos al cliente cuando pide cursos (incluye alumnos ya mapeados).
        public record CursoDTO(
                        Long id,
                        String nombre,
                        String descripcion,
                        @JsonFormat(pattern = "dd/MM/yyyy") LocalDate fechaInicio,
                        @JsonFormat(pattern = "dd/MM/yyyy") LocalDate fechaFin,
                        List<AlumnoDTO> alumnos) {
        }
        // Lo que esperamos cuando creamos un curso por JSON.
        public record CursoCrearDTO(
                        @NotBlank String nombre,
                        @Size(max = 255) String descripcion,
                        @NotNull @JsonFormat(pattern = "dd/MM/yyyy") LocalDate fechaInicio,
                        @NotNull @JsonFormat(pattern = "dd/MM/yyyy") LocalDate fechaFin) {
        }
        // Lo que esperamos cuando crean un curso por JSON.
        public record CursoActualizarDTO(
                        @NotBlank String nombre,
                        @Size(max = 255) String descripcion,
                        @NotNull @JsonFormat(pattern = "dd/MM/yyyy") LocalDate fechaInicio,
                        @NotNull @JsonFormat(pattern = "dd/MM/yyyy") LocalDate fechaFin) {
        }
}
