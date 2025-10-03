package es.jemwsg.cursos.service;

import es.jemwsg.cursos.config.error.ExcepcionNoEncontrado;
import es.jemwsg.cursos.model.Curso;
import es.jemwsg.cursos.repository.RepositorioAlumno;
import es.jemwsg.cursos.repository.RepositorioCurso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de dominio para CURSOS.
 *
 * Responsabilidades:
 * - Orquestación de repositorios.
 * - Excepciones de dominio (ExcepcionNoEncontrado) para recursos inexistentes.
 *
 * Notas de integridad y cascadas:
 * - La entidad Curso tiene @OneToMany(cascade = ALL, orphanRemoval = true).
 *   Eso implica que al borrar un curso, se borran sus alumnos (coherente para la demo).
 * - El nombre es único por BD: si intentas duplicar, lanzará una DataIntegrityViolationException.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CursoServicio {
    private final RepositorioCurso repo;
    // Listar todos los cursos, en un proyecto grande añadiriamos paginación y filtros adicionales
    public List<Curso> listar() {
        return repo.findAll();
    }
    // Obtenemos curso por id o devuelve ExcepcionNoEncontrado
    public Curso obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ExcepcionNoEncontrado("Curso no encontrado"));
    }
    // Creamos curso
    public Curso crear(Curso c) {
        return repo.save(c);
    }
    // Actualizamos curso
    public Curso actualizar(Long id, Curso data) {
        Curso c = obtener(id);
        c.setNombre(data.getNombre());
        c.setDescripcion(data.getDescripcion());
        c.setFechaInicio(data.getFechaInicio());
        c.setFechaFin(data.getFechaFin());
        return repo.save(c);
    }
    // Eliminamos curso
    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }
}
