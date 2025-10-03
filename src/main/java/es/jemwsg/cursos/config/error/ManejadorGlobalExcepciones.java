package es.jemwsg.cursos.config.error;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;


/**
 * Captura excepciones comunes y devuelve una respuesta JSON unificada (ErrorApi).
 * Así todas las APIs devuelven errores consistentes.
 */
@RestControllerAdvice
public class ManejadorGlobalExcepciones {
    /**
     * 404: el recurso pedido no existe (por ejemplo, id que no está en BBDD).
     */
    @ExceptionHandler(ExcepcionNoEncontrado.class)
    public ResponseEntity<ErrorApi> noEncontrado(ExcepcionNoEncontrado ex, HttpServletRequest req) {
        var err = new ErrorApi(Instant.now(), 404, "Not Found", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }
    /**
     * 400: error de validación (@Valid) en los DTO (campos obligatorios, formatos, etc).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList().toString();
        var err = new ErrorApi(Instant.now(), 400, "Bad Request", msg, req.getRequestURI());
        return ResponseEntity.badRequest().body(err);
    }
    /**
     * 400: peticiones mal formadas o con datos no válidos que lanzan IllegalArgumentException.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorApi> peticionInvalida(IllegalArgumentException ex, HttpServletRequest req) {
        var err = new ErrorApi(Instant.now(), 400, "Bad Request", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.badRequest().body(err);
    }
    /**
     * 409: conflictos de integridad (duplicados únicos, FK, etc.).
     * Incluye IllegalStateException y DataIntegrityViolationException.
     */
    @ExceptionHandler({IllegalStateException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErrorApi> conflicto(RuntimeException ex, HttpServletRequest req) {
        var err = new ErrorApi(Instant.now(), 409, "Conflict", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }
}