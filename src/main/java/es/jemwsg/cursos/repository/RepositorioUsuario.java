package es.jemwsg.cursos.repository;

import es.jemwsg.cursos.model.UsuarioAplicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repositorio de usuarios: CRUD + búsqueda por username para autenticación
public interface RepositorioUsuario extends JpaRepository<UsuarioAplicacion, Long> {
    // Busca un usuario por su username
    Optional<UsuarioAplicacion> findByUsername(String username);
}