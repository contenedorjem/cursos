package es.jemwsg.cursos.config.error;

/**
 * Excepción simple para marcar recursos no encontrados.
 * La captura el ManejadorGlobalExcepciones y devuelve 404 JSON.
 */
public class ExcepcionNoEncontrado extends RuntimeException {
    public ExcepcionNoEncontrado(String msg) {
        super(msg);
    }
}