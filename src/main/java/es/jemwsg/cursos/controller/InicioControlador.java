package es.jemwsg.cursos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controlador MVC “de arranque”.
 * Redirige la raíz (/) a la página estática de login.
 * Útil para que quien arranca la app llegue rápido al login y luego a Swagger.
 */
@Controller
public class InicioControlador {

  @GetMapping("/")
  public RedirectView raiz() {
    // Redirige a la página de login estática
    return new RedirectView("/login.html");
  }
}