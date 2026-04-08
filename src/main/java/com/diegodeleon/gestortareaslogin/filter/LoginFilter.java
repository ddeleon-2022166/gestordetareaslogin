package com.diegodeleon.gestortareaslogin.filter;

import com.diegodeleon.gestortareaslogin.model.Usuario;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(1)
public class LoginFilter implements Filter {

    private static final String[] RUTAS_PUBLICAS = {
            "/", "/home", "/auth/login", "/auth/registro",
            "/auth/procesar-login", "/auth/procesar-registro",
            "/css/", "/js/", "/images/"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Verificar si la ruta es pública
        boolean isPublicRoute = false;
        for (String publicPath : RUTAS_PUBLICAS) {
            if (path.startsWith(contextPath + publicPath) || path.equals(contextPath + "/")) {
                isPublicRoute = true;
                break;
            }
        }

        // Verificar si hay usuario en sesión
        boolean isLoggedIn = (session != null && session.getAttribute("usuario") != null);

        if (isPublicRoute || isLoggedIn) {
            // Si es ruta pública o está logueado, continuar
            chain.doFilter(request, response);
        } else {
            // Si no está logueado y no es ruta pública, redirigir al login
            httpResponse.sendRedirect(contextPath + "/auth/login");
        }
    }
}
