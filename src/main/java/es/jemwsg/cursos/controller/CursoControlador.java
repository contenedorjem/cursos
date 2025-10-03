package es.jemwsg.cursos.controller;

import es.jemwsg.cursos.dto.CursoDTOs.*;
import es.jemwsg.cursos.dto.mapper.MapeadorCurso;
import es.jemwsg.cursos.model.Curso;
import es.jemwsg.cursos.service.CursoServicio;
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
 * Controlador REST de Cursos.
 * Expone endpoints CRUD.
 * Seguridad:
 *  - USER y ADMIN pueden leer (GET).
 *  - Solo ADMIN puede crear/actualizar/eliminar (POST/PUT/DELETE).
 * Conversión:
 *  - Usamos MapeadorCurso para transformar Entidad -> DTO (evitamos exponer JPA).
 */
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Tag(
    name = "Cursos",
    description = "Operaciones CRUD de cursos. GET (ADMIN, USER); POST/PUT/DELETE (solo ADMIN)."
)
public class CursoControlador {

    private final CursoServicio servicio;
    private final MapeadorCurso mapper;
    // Listar cursos.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Listar cursos (USER+ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = CursoDTO.class)))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Prohibido")
    })
    public List<CursoDTO> listar() {
        return servicio.listar().stream().map(mapper::aDTO).toList();
    }
    //obtener un curso por id.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Obtener curso por id (USER+ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = CursoDTO.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Prohibido")
    })
    public CursoDTO obtener(@PathVariable Long id) {
        return mapper.aDTO(servicio.obtener(id));
    }
    // Crear curso.
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear curso (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creado",
            content = @Content(schema = @Schema(implementation = CursoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "409", description = "Conflicto (únicos/PK)",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Prohibido")
    })
    public CursoDTO crear(@RequestBody @Valid CursoCrearDTO dto) {
        var entidad = Curso.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .fechaInicio(dto.fechaInicio())
                .fechaFin(dto.fechaFin())
                .build();
        return mapper.aDTO(servicio.crear(entidad));
    }

    // Actualizar curso.
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar curso (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado",
            content = @Content(schema = @Schema(implementation = CursoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado",
            content = @Content(schema = @Schema(implementation = ErrorApi.class))),
        @ApiResponse(responseCode = "409", description = "Conflicto (únicos/PK)",
            content = @Content(schema = @Schema(implementation = ErrorApi.class)))
    })
    public CursoDTO actualizar(@PathVariable Long id, @RequestBody @Valid CursoActualizarDTO dto) {
        var entidad = Curso.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .fechaInicio(dto.fechaInicio())
                .fechaFin(dto.fechaFin())
                .build();
        return mapper.aDTO(servicio.actualizar(id, entidad));
    }
    // Eliminar curso.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar curso (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminado"),
        @ApiResponse(responseCode = "404", description = "No encontrado",
            content = @Content(schema = @Schema(implementation = ErrorApi.class)))
    })
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
