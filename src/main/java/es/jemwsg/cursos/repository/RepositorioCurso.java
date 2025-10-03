package es.jemwsg.cursos.repository;

import es.jemwsg.cursos.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repositorio de cursos: CRUD + utilidades para validar unicidad por nombre
public interface RepositorioCurso extends JpaRepository<Curso, Long> {
    // Obtiene un curso por nombre (ignorando mayúsculas/minúsculas)
    Optional<Curso> findByNombreIgnoreCase(String nombre);
}