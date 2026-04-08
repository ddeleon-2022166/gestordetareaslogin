package com.diegodeleon.gestortareaslogin.repository;

import com.diegodeleon.gestortareaslogin.model.Tarea;
import com.diegodeleon.gestortareaslogin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByUsuarioOrderByFechaLimiteAsc(Usuario usuario);

    List<Tarea> findByUsuarioAndEstadoOrderByFechaLimiteAsc(Usuario usuario, Tarea.EstadoTarea estado);

    List<Tarea> findByUsuarioAndCategoriaIdOrderByFechaLimiteAsc(Usuario usuario, Long categoriaId);

    Optional<Tarea> findByIdAndUsuario(Long id, Usuario usuario);

    @Query("SELECT t FROM Tarea t WHERE t.usuario = :usuario AND t.estado = 'PENDIENTE' ORDER BY t.fechaLimite ASC")
    List<Tarea> findTareasPendientes(@Param("usuario") Usuario usuario);

    @Query("SELECT COUNT(t) FROM Tarea t WHERE t.usuario = :usuario AND t.estado = :estado")
    long countByUsuarioAndEstado(@Param("usuario") Usuario usuario, @Param("estado") Tarea.EstadoTarea estado);
}
