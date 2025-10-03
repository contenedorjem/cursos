package es.jemwsg.cursos.controller;

import es.jemwsg.cursos.dto.AlumnoDTOs.*;
import es.jemwsg.cursos.dto.mapper.MapeadorAlumno;
import es.jemwsg.cursos.model.Alumno;
import es.jemwsg.cursos.service.AlumnoServicio;
import es.jemwsg.cursos.config.error.ErrorApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;

import java.util.List;
/**
 * Controlador REST de Alumnos.
 * Expone endpoints CRUD.
 * Seguridad:
 *  - USER y ADMIN pueden leer (GET).
 *  - Solo ADMIN puede crear/actualizar/eliminar (POST/PUT/DELETE).
 * Conversión:
 *  - Usamos MapeadorAlumno para convertir Entidad <-> DTO.
 */
@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
@Tag(
    name = "Alumnos",
    description = "Operaciones CRUD de alumnos. GET (ADMIN, USER); POST/PUT/DELETE (solo ADMIN)."
)
public class AlumnoControlador {
    // Servicio de negocio para alumnos
    private final AlumnoServicio servicio;
    // Mapeador entidad->DTO para no exponer entidades JPA
    private final MapeadorAlumno mapper;

    // Listado completo de Alumnos
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Listar alumnos (USER+ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = AlumnoDTO.class)))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Prohibido")
    })
    public List<AlumnoDTO> listar() {
        return servicio.listar().stream().map(mapper::aDTO).toList();
    }

    // Obtener alumno por id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Obtener alumno por id (USER+ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = AlumnoDTO.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Prohibido")
    })
    public AlumnoDTO obtener(@PathVariable Long id) {
        return mapper.aDTO(servicio.obtener(id));
    }

    // Crear alumno
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear alumno (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creado",
            content = @Content(schema = @Schema(implementation = AlumnoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "409", description = "Conflicto (únicos/PK)",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Prohibido")
    })
    public AlumnoDTO crear(@RequestBody @Valid AlumnoCrearDTO dto) {
        var entidad = Alumno.builder()
                .nombre(dto.nombre())
                .apellidos(dto.apellidos())
                .email(dto.email())
                .fechaNacimiento(dto.fechaNacimiento())
                .telefono(dto.telefono())
                .build();
        return mapper.aDTO(servicio.crear(entidad, dto.cursoId()));
    }

    // Actualizar alumno JSON
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar alumno (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado",
            content = @Content(schema = @Schema(implementation = AlumnoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "409", description = "Conflicto (únicos/PK)",
            content = @Content(schema = @Schema(implementation = ErrorApi.class)))
    })
    public AlumnoDTO actualizar(@PathVariable Long id, @RequestBody @Valid AlumnoActualizarDTO dto) {
        var entidad = Alumno.builder()
                .nombre(dto.nombre())
                .apellidos(dto.apellidos())
                .email(dto.email())
                .fechaNacimiento(dto.fechaNacimiento())
                .telefono(dto.telefono())
                .build();
        return mapper.aDTO(servicio.actualizar(id, entidad, dto.cursoId()));
    }

    // Eliminar alumno
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar alumno (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminado"),
        @ApiResponse(responseCode = "404", description = "No encontrado",
            content = @Content(schema = @Schema(implementation = ErrorApi.class)))
    })
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
