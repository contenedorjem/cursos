package es.jemwsg.cursos.config.seguridad;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * Servicio que se encarga de crear y validar tokens JWT.
 * Aquí guardamos la clave de firma y el tiempo de expiración.
 */
@Service
public class ServicioJwt {
    private final Key key;
    private final long expMs;
    /**
     * Construye el servicio leyendo la clave y la expiración del application.properties.
     * - app.jwt.secret: cadena larga y aleatoria (mínimo 32 bytes para HS256).
     * - app.jwt.expiration-minutes: minutos que durará cada token.
     */
    public ServicioJwt(@Value("${app.jwt.secret}") String secret,
                       @Value("${app.jwt.expiration-minutes}") long minutos) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMs = minutos * 60 * 1000;
    }

    /**
     * Genera un JWT para el usuario:
     * - subject = username
     * - claim "roles" = lista de roles del usuario
     * - iat/exp = emisión y caducidad
     */
    public String generarToken(UserDetails usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .setSubject(usuario.getUsername())
                .addClaims(Map.of("roles", usuario.getAuthorities().stream().map(a -> a.getAuthority()).toList()))
                .setIssuedAt(Date.from(ahora))
                .setExpiration(Date.from(ahora.plusMillis(expMs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * Extrae el nombre de usuario (subject) a partir del token.
     */
    public String extraerUsuario(String token) {
        return parser(token).getBody().getSubject();
    }
    /**
     * Comprueba si el token es válido para ese usuario:
     * - la firma es correcta
     * - el subject coincide
     * - la fecha de expiración no ha pasado
     */
    public boolean esValido(String token, UserDetails user) {
        try {
            var jws = parser(token);
            var body = jws.getBody();
            return user.getUsername().equals(body.getSubject()) && body.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // La firma no es correcta si el token está mal formado o ha expirado.
            return false;
        }
    }
    /**
     * Parser interno para validar firma y obtener las claims.
     */
    private Jws<Claims> parser(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}