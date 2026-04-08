package com.diegodeleon.gestortareaslogin.service;

import com.diegodeleon.gestortareaslogin.model.Categoria;
import com.diegodeleon.gestortareaslogin.model.Tarea;
import com.diegodeleon.gestortareaslogin.model.Usuario;
import com.diegodeleon.gestortareaslogin.repository.CategoriaRepository;
import com.diegodeleon.gestortareaslogin.repository.TareaRepository;
import com.diegodeleon.gestortareaslogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Tarea guardar(Tarea tarea, Long usuarioId) {
        // Recargar el usuario desde la base de datos
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        tarea.setUsuario(usuario);

        // Si tiene categoría, recargarla desde la base de datos
        if (tarea.getCategoria() != null && tarea.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(tarea.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            // Verificar que la categoría pertenece al usuario
            if (!categoria.getUsuario().getId().equals(usuarioId)) {
                throw new RuntimeException("La categoría no pertenece al usuario");
            }

            tarea.setCategoria(categoria);
        } else {
            tarea.setCategoria(null);
        }

        return tareaRepository.save(tarea);
    }

    public Tarea guardar(Tarea tarea) {
        // Método simple para mantener compatibilidad
        if (tarea.getUsuario() != null && tarea.getUsuario().getId() != null) {
            return guardar(tarea, tarea.getUsuario().getId());
        }
        return tareaRepository.save(tarea);
    }

    public List<Tarea> listarPorUsuario(Usuario usuario) {
        return tareaRepository.findByUsuarioOrderByFechaLimiteAsc(usuario);
    }

    public List<Tarea> listarPendientes(Usuario usuario) {
        return tareaRepository.findTareasPendientes(usuario);
    }

    public List<Tarea> listarPorEstado(Usuario usuario, Tarea.EstadoTarea estado) {
        return tareaRepository.findByUsuarioAndEstadoOrderByFechaLimiteAsc(usuario, estado);
    }

    public List<Tarea> listarPorCategoria(Usuario usuario, Long categoriaId) {
        return tareaRepository.findByUsuarioAndCategoriaIdOrderByFechaLimiteAsc(usuario, categoriaId);
    }

    public Optional<Tarea> buscarPorId(Long id) {
        return tareaRepository.findById(id);
    }

    public Optional<Tarea> buscarPorIdYUsuario(Long id, Usuario usuario) {
        return tareaRepository.findByIdAndUsuario(id, usuario);
    }

    public void eliminar(Long id) {
        tareaRepository.deleteById(id);
    }

    public Tarea actualizar(Tarea tarea) {
        return tareaRepository.save(tarea);
    }

    public Tarea cambiarEstado(Long id, Tarea.EstadoTarea nuevoEstado, Usuario usuario) {
        Optional<Tarea> tareaOpt = buscarPorIdYUsuario(id, usuario);
        if (tareaOpt.isPresent()) {
            Tarea tarea = tareaOpt.get();
            tarea.setEstado(nuevoEstado);
            if (nuevoEstado == Tarea.EstadoTarea.COMPLETADA) {
                tarea.setFechaCompletada(java.time.LocalDateTime.now());
            }
            return tareaRepository.save(tarea);
        }
        return null;
    }

    public long contarTareasPorEstado(Usuario usuario, Tarea.EstadoTarea estado) {
        return tareaRepository.countByUsuarioAndEstado(usuario, estado);
    }
}