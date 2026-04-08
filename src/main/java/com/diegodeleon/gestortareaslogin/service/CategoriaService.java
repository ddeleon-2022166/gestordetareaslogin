package com.diegodeleon.gestortareaslogin.service;

import com.diegodeleon.gestortareaslogin.model.Categoria;
import com.diegodeleon.gestortareaslogin.model.Usuario;
import com.diegodeleon.gestortareaslogin.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarPorUsuario(Usuario usuario) {
        return categoriaRepository.findByUsuarioOrderByNombreAsc(usuario);
    }

    public List<Categoria> listarPorUsuarioId(Long usuarioId) {
        return categoriaRepository.findByUsuarioIdOrderByNombreAsc(usuarioId);
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> buscarPorIdYUsuario(Long id, Usuario usuario) {
        return categoriaRepository.findByIdAndUsuario(id, usuario);
    }

    public boolean existeNombreParaUsuario(String nombre, Usuario usuario) {
        return categoriaRepository.existsByNombreAndUsuario(nombre, usuario);
    }

    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }

    public Categoria actualizar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
}