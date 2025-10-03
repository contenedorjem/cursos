package es.jemwsg.cursos.config.seguridad;

/**
 * Roles de la aplicación. Usamos el prefijo "ROLE_" porque es lo que espera Spring Security.
 * - ROLE_ADMIN: puede hacer todo (GET/POST/PUT/DELETE).
 * - ROLE_USER: sólo lectura (GET).
 */
public enum Rol { 
    ROLE_ADMIN, ROLE_USER 
}