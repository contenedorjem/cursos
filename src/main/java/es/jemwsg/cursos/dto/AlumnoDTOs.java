package es.jemwsg.cursos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

// DTOs de ALUMNO.
// - Usamos records para que sean inmutables y concisos.
// - @JsonFormat en fechas para usar formato español (dd/MM/yyyy).
// - En AlumnoDTO devolvemos solo el id del curso para no anidar curso completo.
public class AlumnoDTOs {
        // Lo que devolvemos al cliente para un alumno concreto.
        public record AlumnoDTO(
                        Long id,
                        String nombre,
                        String apellidos,
                        String email,
                        @JsonFormat(pattern = "dd/MM/yyyy") LocalDate fechaNacimiento,
                        String telefono,
                        Long cursoId) {
        }
        // Datos de entrada para crear alumno.  
        public record AlumnoCrearDTO(
                        @NotBlank String nombre,
                        @NotBlank String apellidos,
                        @Email String email,
                        @Past @JsonFormat(pattern = "dd/MM/yyyy") LocalDate fechaNacimiento,
                        String telefono,
                        @NotNull Long cursoId) {
        }
        // Datos de entrada para actualizar alumno.
        public record AlumnoActualizarDTO(
                        @NotBlank String nombre,
                        @NotBlank String apellidos,
                        @Email String email,
                        @Past @JsonFormat(pattern = "dd/MM/yyyy") LocalDate fechaNacimiento,
                        String telefono,
                        @NotNull Long cursoId) {
        }
}