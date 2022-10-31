package com.example.demo.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.service.IProductoService;
import com.example.demo.service.IUploadFileService;
import com.example.demo.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IUploadFileService uFileService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("")
    public String show(Model model) {
        model.addAttribute("productos", productoService.findAll());
        return "productos/show";
    }

    @GetMapping("/create")
    public String create() {
        return "productos/create";
    }

    @PostMapping("/save")
    public String save(Producto producto, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
        LOGGER.info("Este es el objeto producto{}", producto);
        
        Usuario u = usuarioService.findByid(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
        producto.setUsuario(u);

        // imagen
        if (producto.getId() == null) { // cuando se crea un producto
            String nombreImagen = uFileService.saveImage(file);
            producto.setImagen(nombreImagen);
        } else {

        }

        productoService.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Producto producto = new Producto();
        Optional<Producto> optionalProducto = productoService.get(id);
        producto = optionalProducto.get();
        LOGGER.info("Producto Buscado: {}", producto);
        model.addAttribute("producto", producto);
        return "productos/edit";
    }

    @PostMapping("/update")
    public String update(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {

        Producto p = new Producto();
        p = productoService.get(producto.getId()).get();

        if (file.isEmpty()) {// editamos el producto pero no cambiamos la images

            producto.setImagen(p.getImagen());
        } else {// editamos el producto y se cambia la imagen
            // eliminar cuando no sea la imagen por defecto
            if (!p.getImagen().equals("default.jpg")) {
                uFileService.deleteImage(p.getImagen());
            }

            String nombreImagen = uFileService.saveImage(file);
            producto.setImagen(nombreImagen);
        }
        producto.setUsuario(p.getUsuario());
        productoService.update(producto);
        return "redirect:/productos";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {

        Producto p = new Producto();
        p = productoService.get(id).get();

        // eliminar cuando no sea la imagen por defecto
        if (!p.getImagen().equals("default.jpg")) {
            uFileService.deleteImage(p.getImagen());
        }

        productoService.delete(id);
        return ("redirect:/productos");
    }
}
