package com.diegodeleon.gestortareaslogin.controller;

import com.diegodeleon.gestortareaslogin.model.Categoria;
import com.diegodeleon.gestortareaslogin.model.Usuario;
import com.diegodeleon.gestortareaslogin.service.CategoriaService;
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
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("categorias", categoriaService.listarPorUsuario(usuario));
        return "categorias/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("colores", new String[]{
                "#3498db", "#2ecc71", "#e74c3c", "#f39c12", "#9b59b6", "#1abc9c"
        });
        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Categoria categoria,
                          BindingResult result,
                          HttpSession session,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (result.hasErrors()) {
            model.addAttribute("colores", new String[]{
                    "#3498db", "#2ecc71", "#e74c3c", "#f39c12", "#9b59b6", "#1abc9c"
            });
            return "categorias/formulario";
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        categoria.setUsuario(usuario);

        // Verificar nombre duplicado (solo para nuevas categorías)
        if (categoria.getId() == null &&
                categoriaService.existeNombreParaUsuario(categoria.getNombre(), usuario)) {
            result.rejectValue("nombre", "error.categoria",
                    "Ya existe una categoría con ese nombre");
            return "categorias/formulario";
        }

        categoriaService.guardar(categoria);
        redirectAttributes.addFlashAttribute("success",
                "Categoría guardada exitosamente");
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          Model model,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<Categoria> categoriaOpt = categoriaService.buscarPorIdYUsuario(id, usuario);

        if (categoriaOpt.isPresent()) {
            model.addAttribute("categoria", categoriaOpt.get());
            model.addAttribute("colores", new String[]{
                    "#3498db", "#2ecc71", "#e74c3c", "#f39c12", "#9b59b6", "#1abc9c"
            });
            return "categorias/formulario";
        } else {
            redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
            return "redirect:/categorias";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<Categoria> categoriaOpt = categoriaService.buscarPorIdYUsuario(id, usuario);

        if (categoriaOpt.isPresent()) {
            try {
                categoriaService.eliminar(id);
                redirectAttributes.addFlashAttribute("success",
                        "Categoría eliminada exitosamente");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error",
                        "No se puede eliminar la categoría porque tiene tareas asociadas");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
        }

        return "redirect:/categorias";
    }
}
