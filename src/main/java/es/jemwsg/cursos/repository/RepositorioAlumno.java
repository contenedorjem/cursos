package es.jemwsg.cursos.repository;

import es.jemwsg.cursos.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repositorio de alumnos: CRUD + checks por email y métrica de alumnos por curso
public interface RepositorioAlumno extends JpaRepository<Alumno, Long> {
    // Busca un alumno por email (exact match)
    Optional<Alumno> findByEmail(String email);
}