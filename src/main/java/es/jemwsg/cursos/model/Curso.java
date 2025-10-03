package es.jemwsg.cursos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Curso
 * - Representa un curso que puede tener VARIOS alumnos (1:N).
 * - Para simplificar el ejercicio y devolver JSON sin errores de lazy loading,
 *   usamos fetch = EAGER en la colección.
 */
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrementa con cada entrada
    private Long id;
    @Column(nullable = false, unique = true) // Nos obliga a dar un valor
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    // Relación de uno a muchos, fetch eager para mejorar el rendimiento, al borrar un curso se borran sus alumnos asociados
    @OneToMany(
        mappedBy = "curso",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @Builder.Default
    private List<Alumno> alumnos = new ArrayList<>();
}
