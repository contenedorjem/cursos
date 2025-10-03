package es.jemwsg.cursos.config.seguridad;

import es.jemwsg.cursos.model.UsuarioAplicacion;
import es.jemwsg.cursos.repository.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Adapta nuestra entidad UsuarioAplicacion al modelo UserDetails de Spring Security.
 * Spring nos llamará aquí cuando alguien intente autenticarse.
 */
@Service
@RequiredArgsConstructor
public class ServicioDetallesUsuario implements UserDetailsService {
    private final RepositorioUsuario repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos el usuario en BBDD; si no existe, lanzamos excepción estándar
        UsuarioAplicacion u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        // Devolvemos un User de Spring con su password y sus roles
        return new User(u.getUsername(), u.getPassword(), u.isEnabled(), true, true, true,
                List.of(new SimpleGrantedAuthority(u.getRol().name())));
    }
}