package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Orden;
import com.example.demo.model.Usuario;
import com.example.demo.service.IOrdenService;
import com.example.demo.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IOrdenService ordenService;

    BCryptPasswordEncoder passEncode = new BCryptPasswordEncoder();

    private final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);

    //usuario/registro
    @GetMapping("/registro")
    public String create(){
        
        return "usuario/registro";
    }

    @PostMapping("/save")
    public String save(Usuario usuario){
        LOGGER.info("usuario registro: {}", usuario);
        usuario.setTipo("USER");
        //encripta, modifica y guarda la contraseña del usuario
        usuario.setPsw(passEncode.encode(usuario.getPsw()));
        usuarioService.save(usuario);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "usuario/login";
    }

    @PostMapping("/acceder")
    public String acceder(Usuario usuario, HttpSession session){
        LOGGER.info("Credenciales de acceso: {}", usuario);
        Optional<Usuario> user = usuarioService.findByEmail(usuario.getEmail());
        
        //LOGGER.info("usuario obtenido: {}", user.get());
        
        if(user.isPresent()){
            session.setAttribute("idUsuario", user.get().getId());
            if(user.get().getTipo().equals("ADMIN")){
                return "redirect:/administrador";
            }else{
                return "redirect:/";
            }
        }else{
            LOGGER.info("usuario no existe");
        }
        //usuarioService.findByEmail(usuario.getEmail());

        return "redirect:/";
    }

    @GetMapping("/compras")
    public String compras(HttpSession session, Model model){
        model.addAttribute("sesion", session.getAttribute("idUsuario"));
        Usuario usuario = usuarioService.findByid(Integer.parseInt(session.getAttribute("idUsuario").toString())).get();
        List<Orden> ordenes = ordenService.findByUsuario(usuario);

        model.addAttribute("ordenes", ordenes);
        return "usuario/compras";
    }

    @GetMapping("/detalle/{id}")
    public String detalleCompra(@PathVariable Integer id, Model model, HttpSession session){
        model.addAttribute("sesion", session.getAttribute("idUsuario"));
        LOGGER.info("id de la orden: {}", id);
        Optional<Orden> orden = ordenService.findById(id);
         
        model.addAttribute("detalles", orden.get().getDetalle());

        return "usuario/detalleCompra";
    }

    @GetMapping("/cerrar")
    public String cerrarSesion(HttpSession session){
        session.removeAttribute("idUsuario");
        return "redirect:/";
    }

}
