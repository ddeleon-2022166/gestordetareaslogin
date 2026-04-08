package com.diegodeleon.gestortareaslogin.controller;

import com.diegodeleon.gestortareaslogin.model.Categoria;
import com.diegodeleon.gestortareaslogin.model.Tarea;
import com.diegodeleon.gestortareaslogin.model.Usuario;
import com.diegodeleon.gestortareaslogin.service.CategoriaService;
import com.diegodeleon.gestortareaslogin.service.TareaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    @Autowired
    private TareaService tareaService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model, HttpSession session,
                         @RequestParam(required = false) String filtro,
                         @RequestParam(required = false) Long categoriaId) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (categoriaId != null && categoriaId > 0) {
            model.addAttribute("tareas",
                    tareaService.listarPorCategoria(usuario, categoriaId));
            model.addAttribute("filtroActivo", "categoria");
        } else if ("pendientes".equals(filtro)) {
            model.addAttribute("tareas",
                    tareaService.listarPorEstado(usuario, Tarea.EstadoTarea.PENDIENTE));
            model.addAttribute("filtroActivo", "pendientes");
        } else if ("en_progreso".equals(filtro)) {
            model.addAttribute("tareas",
                    tareaService.listarPorEstado(usuario, Tarea.EstadoTarea.EN_PROGRESO));
            model.addAttribute("filtroActivo", "en_progreso");
        } else if ("completadas".equals(filtro)) {
            model.addAttribute("tareas",
                    tareaService.listarPorEstado(usuario, Tarea.EstadoTarea.COMPLETADA));
            model.addAttribute("filtroActivo", "completadas");
        } else {
            model.addAttribute("tareas", tareaService.listarPorUsuario(usuario));
            model.addAttribute("filtroActivo", "todas");
        }

        model.addAttribute("categorias", categoriaService.listarPorUsuario(usuario));
        return "tareas/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioCrear(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("tarea", new Tarea());
        model.addAttribute("categorias", categoriaService.listarPorUsuario(usuario));
        model.addAttribute("prioridades", Tarea.Prioridad.values());

        return "tareas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Tarea tarea,
                          BindingResult result,
                          HttpSession session,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        Usuario usuarioSession = (Usuario) session.getAttribute("usuario");

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarPorUsuario(usuarioSession));
            model.addAttribute("prioridades", Tarea.Prioridad.values());
            return "tareas/formulario";
        }

        try {
            // Usar el nuevo método que recarga las entidades
            tareaService.guardar(tarea, usuarioSession.getId());
            redirectAttributes.addFlashAttribute("success", "Tarea guardada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }

        return "redirect:/tareas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          Model model,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<Tarea> tareaOpt = tareaService.buscarPorIdYUsuario(id, usuario);

        if (tareaOpt.isPresent()) {
            model.addAttribute("tarea", tareaOpt.get());
            model.addAttribute("categorias", categoriaService.listarPorUsuario(usuario));
            model.addAttribute("prioridades", Tarea.Prioridad.values());
            return "tareas/formulario";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tarea no encontrada");
            return "redirect:/tareas";
        }
    }

    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam Tarea.EstadoTarea estado,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Tarea tarea = tareaService.cambiarEstado(id, estado, usuario);

        if (tarea != null) {
            redirectAttributes.addFlashAttribute("success",
                    "Estado actualizado a: " + estado);
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo actualizar el estado");
        }

        return "redirect:/tareas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<Tarea> tareaOpt = tareaService.buscarPorIdYUsuario(id, usuario);

        if (tareaOpt.isPresent()) {
            tareaService.eliminar(id);
            redirectAttributes.addFlashAttribute("success",
                    "Tarea eliminada exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Tarea no encontrada");
        }

        return "redirect:/tareas";
    }
}
