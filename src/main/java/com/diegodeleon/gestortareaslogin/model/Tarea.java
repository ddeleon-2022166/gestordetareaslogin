package com.diegodeleon.gestortareaslogin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String titulo;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha límite es obligatoria")
    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTarea estado = EstadoTarea.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad = Prioridad.MEDIA;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // Enums para estado y prioridad
    public enum EstadoTarea {
        PENDIENTE, EN_PROGRESO, COMPLETADA
    }

    public enum Prioridad {
        BAJA, MEDIA, ALTA
    }

    // Métodos helper para la vista
    public String getEstadoBadge() {
        return switch (estado) {
            case PENDIENTE -> "bg-warning";
            case EN_PROGRESO -> "bg-info";
            case COMPLETADA -> "bg-success";
        };
    }

    public String getPrioridadBadge() {
        return switch (prioridad) {
            case BAJA -> "bg-secondary";
            case MEDIA -> "bg-primary";
            case ALTA -> "bg-danger";
        };
    }

    public boolean isAtrasada() {
        return estado != EstadoTarea.COMPLETADA &&
                fechaLimite.isBefore(LocalDate.now());
    }
}