package es.jemwsg.cursos.dto.mapper;

import es.jemwsg.cursos.dto.AlumnoDTOs.AlumnoDTO;
import es.jemwsg.cursos.model.Alumno;
import org.springframework.stereotype.Component;

// Mapper de ALUMNO: entidad -> DTO.
@Component
public class MapeadorAlumno {
    // Convierte Alumno en AlumnoDTO (si no hay curso, cursoId = null).
    public AlumnoDTO aDTO(Alumno s) {
        return new AlumnoDTO(s.getId(), s.getNombre(), s.getApellidos(), s.getEmail(), s.getFechaNacimiento(),
                s.getTelefono(), s.getCurso() != null ? s.getCurso().getId() : null);
    }
}