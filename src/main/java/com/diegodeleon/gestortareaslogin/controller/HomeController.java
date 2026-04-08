package com.diegodeleon.gestortareaslogin.controller;

import com.diegodeleon.gestortareaslogin.model.Tarea;
import com.diegodeleon.gestortareaslogin.model.Usuario;
import com.diegodeleon.gestortareaslogin.service.CategoriaService;
import com.diegodeleon.gestortareaslogin.service.TareaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private TareaService tareaService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("usuario") != null) {
            return "redirect:/home";
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/auth/login";
        }

        try {
            // Estadísticas para el dashboard
            model.addAttribute("tareasPendientes",
                    tareaService.listarPorEstado(usuario, Tarea.EstadoTarea.PENDIENTE));

            model.addAttribute("categorias",
                    categoriaService.listarPorUsuario(usuario));

            // Contadores
            model.addAttribute("totalPendientes",
                    tareaService.contarTareasPorEstado(usuario, Tarea.EstadoTarea.PENDIENTE));
            model.addAttribute("totalEnProgreso",
                    tareaService.contarTareasPorEstado(usuario, Tarea.EstadoTarea.EN_PROGRESO));
            model.addAttribute("totalCompletadas",
                    tareaService.contarTareasPorEstado(usuario, Tarea.EstadoTarea.COMPLETADA));
            model.addAttribute("totalCategorias",
                    categoriaService.listarPorUsuario(usuario).size());

        } catch (Exception e) {
            // Si hay error, inicializar con valores por defecto
            model.addAttribute("tareasPendientes", new java.util.ArrayList<>());
            model.addAttribute("categorias", new java.util.ArrayList<>());
            model.addAttribute("totalPendientes", 0L);
            model.addAttribute("totalEnProgreso", 0L);
            model.addAttribute("totalCompletadas", 0L);
            model.addAttribute("totalCategorias", 0);
        }

        model.addAttribute("titulo", "Dashboard");
        return "home";
    }
}