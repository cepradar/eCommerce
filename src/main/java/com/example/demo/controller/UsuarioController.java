package com.example.demo.controller;

import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Usuario;
import com.example.demo.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
    @Autowired
    private IUsuarioService usuarioService;

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
}