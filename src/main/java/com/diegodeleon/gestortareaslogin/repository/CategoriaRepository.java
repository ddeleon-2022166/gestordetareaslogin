package com.diegodeleon.gestortareaslogin.repository;

import com.diegodeleon.gestortareaslogin.model.Categoria;
import com.diegodeleon.gestortareaslogin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioOrderByNombreAsc(Usuario usuario);

    List<Categoria> findByUsuarioIdOrderByNombreAsc(Long usuarioId);

    Optional<Categoria> findByIdAndUsuario(Long id, Usuario usuario);

    boolean existsByNombreAndUsuario(String nombre, Usuario usuario);
}