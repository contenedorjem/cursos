package es.jemwsg.cursos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Entidad Alumno
 * - Representa a un alumno que pertenece a UN único curso (ManyToOne).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrementa con cada entrada
    private Long id;
    @Column(nullable = false) // Nos obliga a dar un valor
    private String nombre;
    @Column(nullable = false) // Nos obliga a dar un valor
    private String apellidos;
    @Column(unique = true) // Nos obliga a dar un valor
    private String email;
    private LocalDate fechaNacimiento;
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY) // Relación de muchos a uno, fetch lazy para mejorar el rendimiento
    @JoinColumn(name = "curso_id") // Llave foranea hacia la entidad Curso
    private Curso curso; 
}