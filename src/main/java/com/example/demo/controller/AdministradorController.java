package com.example.demo.controller;

import java.util.List;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Producto;
import com.example.demo.service.IOrdenService;
import com.example.demo.service.IProductoService;
import com.example.demo.service.IUsuarioService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    @Autowired
    private IOrdenService ordenService;
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private IProductoService productoService;

    @GetMapping("")
    public String home(Model model){
        List<Producto> productos = productoService.findAll();
        model.addAttribute("productos", productos);
        return "administrador/home";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model){
        model.addAttribute("usuarios", usuarioService.findAll());
        return "administrador/usuarios";
    }

    @GetMapping("/ordenes")
    public String ordenes(Model model){
        model.addAttribute("ordenes", ordenService.findAll());
        return "administrador/ordenes";
    }
    
}
