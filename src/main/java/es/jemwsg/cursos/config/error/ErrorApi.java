package es.jemwsg.cursos.config.error;

import java.time.Instant;

/**
 * Modelo de error que devolvemos en las respuestas de error:
 * - timestamp: cuándo ocurrió
 * - status: código HTTP
 * - error: motivo corto (Not Found, Bad Request, etc.)
 * - message: detalle del error
 * - path: URL solicitada
 */
public record ErrorApi(Instant timestamp, int status, String error, String message, String path) {
}