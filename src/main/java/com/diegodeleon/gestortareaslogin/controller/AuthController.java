package com.diegodeleon.gestortareaslogin.controller;

import com.diegodeleon.gestortareaslogin.model.Usuario;
import com.diegodeleon.gestortareaslogin.service.UsuarioService;
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
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/login";
    }

    @PostMapping("/procesar-login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioService.login(email, password);

        if (usuarioOpt.isPresent()) {
            session.setAttribute("usuario", usuarioOpt.get());
            session.setMaxInactiveInterval(30 * 60); // 30 minutos
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("error", "Email o contraseña incorrectos");
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/procesar-registro")
    public String procesarRegistro(@Valid @ModelAttribute Usuario usuario,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/registro";
        }

        try {
            usuarioService.registrar(usuario);
            redirectAttributes.addFlashAttribute("success",
                    "¡Registro exitoso! Por favor, inicia sesión.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/registro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
