package es.jemwsg.cursos.dto.mapper;

import es.jemwsg.cursos.dto.CursoDTOs.CursoDTO;
import es.jemwsg.cursos.model.Curso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

// Mapper de CURSO: transforma entidad JPA -> DTO
// El curso viene con alumnos, así que podemos mapearlos sin LazyException.
@Component
@RequiredArgsConstructor
public class MapeadorCurso {
    private final MapeadorAlumno mapeadorAlumno;
    // Convierte Curso en CursoDTO, mapeando también la lista de alumnos a DTO.
    public CursoDTO aDTO(Curso c) {
        List<es.jemwsg.cursos.dto.AlumnoDTOs.AlumnoDTO> alumnos = c.getAlumnos().stream().map(mapeadorAlumno::aDTO)
                .toList();
        return new CursoDTO(c.getId(), c.getNombre(), c.getDescripcion(), c.getFechaInicio(), c.getFechaFin(), alumnos);
    }
}