package es.jemwsg.cursos.controller;

import es.jemwsg.cursos.config.seguridad.ServicioJwt;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
/**
 * Controlador de Autenticación (JWT).
 *
 * Flujo de /login:
 *  1) Recibe usuario/contraseña (JSON).
 *  2) Delegamos en AuthenticationManager para validar credenciales.
 *  3) Cargamos UserDetails y generamos un JWT firmado (ServicioJwt).
 *  4) Enviamos:
 *     - Cookie "JWT" httpOnly (SameSite=Lax) con el token para que el navegador la incluya automáticamente.
 *     - (Opcional) Devolvemos el token en el body por comodidad del cliente (ej.: pruebas en Swagger/Insomnia).
 *
 * Flujo de /logout:
 *  - Sobrescribimos la cookie "JWT" con Max-Age=0 para invalidarla en el navegador.
 *
 * Notas:
 *  - httpOnly: evita acceso a la cookie desde JS (mitiga XSS).
 *  - sameSite=Lax: el navegador no la manda en la mayoría de peticiones cross-site.
 *  - secure=false: en desarrollo permitimos HTTP; en producción debe ser true (HTTPS).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthControlador {
    /** Autenticador de Spring Security (valida usuario/contraseña) */
    private final AuthenticationManager authManager;
    /** Servicio de utilidades JWT (generar/leer claims) */
    private final ServicioJwt jwt;
    /** Carga los UserDetails desde nuestra tabla de usuarios */
    private final UserDetailsService uds;
    /** Minutos de expiración del JWT (inyectado desde properties, 1 hora) */
    @Value("${app.jwt.expiration-minutes}")
    private long expMin;

    public record SolicitudLogin(String username, String password) {}
    public record RespuestaToken(String token) {}
    /**
     * Autentica y genera un JWT. También setea cookie httpOnly con el token.
     */
    @PostMapping("/login")
    public ResponseEntity<RespuestaToken> login(@RequestBody SolicitudLogin req, HttpServletResponse res) {
        // 1) Validar credenciales (lanza excepción si son incorrectas).
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        // 2) Cargar usuario y 3) generar token con roles.
        UserDetails usuario = uds.loadUserByUsername(req.username());
        String token = jwt.generarToken(usuario);
        // 4) Devolver cookie httpOnly + token en body
        ResponseCookie cookie = ResponseCookie.from("JWT", token)
                .httpOnly(true) // Evita acceso a la cookie desde JS.
                .secure(false) // En producción lo pasamos a true (HTTPS)
                .sameSite("Lax") // Evita la mayoría de envíos cross-site.
                .path("/") // Disponible en toda la app
                .maxAge(Duration.ofMinutes(expMin)) // Expiración en minutos.
                .build(); // Genera cookie.
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString()); // Enviamos la cookie.

        return ResponseEntity.ok(new RespuestaToken(token));
    }
    /**
     * Invalida la cookie "JWT" en el navegador (logout stateless).
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0) // Expiración inmediata.
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }
}
