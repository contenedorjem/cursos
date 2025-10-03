package es.jemwsg.cursos.config.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta en cada petición (OncePerRequestFilter) para:
 * 1) Leer el token JWT (cabecera Authorization: Bearer ... o cookie "JWT").
 * 2) Validarlo.
 * 3) Si es correcto, cargar el usuario y ponerlo en el SecurityContext,
 * de modo que las @PreAuthorize funcionen con sus roles.
 */
@Component
@RequiredArgsConstructor
public class FiltroJwtAutenticacion extends OncePerRequestFilter {
  private final ServicioJwt jwt;
  private final ServicioDetallesUsuario detallesUsuario;

  /**
   * Intenta obtener el JWT primero de la cabecera Authorization (Bearer),
   * y si no, de la cookie "JWT".
   */
  private String extraerToken(HttpServletRequest req) {
    String auth = req.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      return auth.substring(7);
    }
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie c : cookies) {
        if ("JWT".equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
          return c.getValue();
        }
      }
    }
    return null;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String token = extraerToken(request);
    // Sólo intentamos autenticar si hay token y aún no hay un usuario en contexto
    if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        String username = jwt.extraerUsuario(token);
        if (username != null) {
          UserDetails user = detallesUsuario.loadUserByUsername(username);
          // Validamos firma/expiración y que el token sea del mismo usuario
          if (jwt.esValido(token, user)) {
            var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Marcamos al usuario como autenticado para el resto del ciclo
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }
      } catch (Exception ignored) {
      // Si algo falla (token inválido, expirado, usuario no existe), seguimos sin autenticar
      }
    }
    // Continuamos con el resto de filtros / controller
    chain.doFilter(request, response);
  }
}
