package es.jemwsg.cursos.service;

import es.jemwsg.cursos.config.error.ExcepcionNoEncontrado;
import es.jemwsg.cursos.model.Alumno;
import es.jemwsg.cursos.model.Curso;
import es.jemwsg.cursos.repository.RepositorioAlumno;
import es.jemwsg.cursos.repository.RepositorioCurso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de dominio para ALUMNOS.
 *
 * Responsabilidades:
 * - Cada alumno pertenece a UN solo curso.
 * - Lanzar excepciones de dominio (ExcepcionNoEncontrado) en lugar de nulls.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AlumnoServicio {
    private final RepositorioAlumno repo;
    private final RepositorioCurso repoCurso;

    // Listar todos los alumnos, en un proyecto grande añadiriamos paginación y filtros adicionales
    public List<Alumno> listar() {
        return repo.findAll();
    }
    //Obtenemos alumno por id o devuelve ExcepcionNoEncontrado
    public Alumno obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ExcepcionNoEncontrado("Alumno no encontrado"));
    }
    //Creamos alumno, si el email ya existe lanzamos excepción, si no exise el curso lanzamos excepción y si no existe el alumno lo creamos
    public Alumno crear(Alumno s, Long cursoId) {
        Curso c = repoCurso.findById(cursoId).orElseThrow(() -> new ExcepcionNoEncontrado("Curso no encontrado"));
        if (s.getEmail() != null) {
            repo.findByEmail(s.getEmail()).ifPresent(existente -> {
                if (existente.getCurso() != null)
                    throw new IllegalStateException("El email ya está asociado a otro curso");
            });
        }
        s.setCurso(c);
        return repo.save(s);
    }
    // Actualizamos alumno, si el email ya existe lanzamos excepción, si no exise el curso lanzamos excepción
    public Alumno actualizar(Long id, Alumno data, Long cursoId) {
        Alumno s = obtener(id);
        Curso c = repoCurso.findById(cursoId).orElseThrow(() -> new ExcepcionNoEncontrado("Curso no encontrado"));
        s.setNombre(data.getNombre());
        s.setApellidos(data.getApellidos());
        s.setEmail(data.getEmail());
        s.setFechaNacimiento(data.getFechaNacimiento());
        s.setTelefono(data.getTelefono());
        s.setCurso(c);
        return repo.save(s);
    }
    // Eliminamos alumno
    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }
}
