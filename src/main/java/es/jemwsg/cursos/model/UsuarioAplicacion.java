package es.jemwsg.cursos.model;

import es.jemwsg.cursos.config.seguridad.Rol;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad UsuarioAplicacion
 * - Usuario simple para autenticación/autorización con Spring Security + JWT.
 * - El password se guarda encriptado con prefijo {noop} para pruebas.
 * - El rol se mapea al enum Rol (ROLE_ADMIN / ROLE_USER).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioAplicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Autoincrementa con cada entrada
    private Long id;
    @Column(nullable = false, unique = true) // Nos obliga a dar un valor único
    private String username;
    private String password;
    @Enumerated(EnumType.STRING) // Guarda el nombre del rol en la BD 
    private Rol rol;
    private boolean enabled = true; // Por defecto, el usuario está activo
}