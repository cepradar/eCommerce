package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.DetalleOrden;
import com.example.demo.model.Orden;
import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

import com.example.demo.service.IDetalleOrdenService;
import com.example.demo.service.IOrdenService;
import com.example.demo.service.IProductoService;

@Controller
@RequestMapping("/")
public class HomeController {

    private final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IOrdenService ordenService;

    @Autowired
    private IDetalleOrdenService detalleOrdenService;

    List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

    // datos de la orden
    Orden orden = new Orden();

    @GetMapping("")
    public String home(Model model, HttpSession session) {
        LOGGER.info("la sesion del usuario es: {}", session.getAttribute("idusuario"));

        
        model.addAttribute("productos", productoService.findAll());
        
        //session
        model.addAttribute("sesion", session.getAttribute("idusuario"));
        return "usuario/home";
    }

    @GetMapping("/productohome/{id}")
    public String productoHome(@PathVariable Integer id, Model model) {
        LOGGER.info("Id producto enviado como parámetro {}", id);
        Producto producto = new Producto();
        Optional<Producto> pOptional = productoService.get(id);
        producto = pOptional.get();

        model.addAttribute("producto", producto);
        return "usuario/productohome";
    }

    @PostMapping("/cart")
    public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model) {
        DetalleOrden detalleOrden = new DetalleOrden();
        Producto producto = new Producto();
        double sumaTotal = 0;

        Optional<Producto> optionalProducto = productoService.get(id);
        producto = optionalProducto.get();
        LOGGER.info("Producto añadido: {}", producto);
        LOGGER.info("Cantidad: {}", cantidad);

        detalleOrden.setCantidad(cantidad);
        detalleOrden.setPrecio(producto.getPrecio());
        detalleOrden.setNombre(producto.getNombre());
        detalleOrden.setTotal(producto.getPrecio() * cantidad);
        detalleOrden.setProducto(producto);

        // validar que el producto no se añada 2 veces
        Integer idProducto = producto.getId();
        boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId().equals(idProducto));
        if (!ingresado) {
            detalles.add(detalleOrden);
        }

        sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

        orden.setTotal(sumaTotal);
        model.addAttribute("Cart", detalles);
        model.addAttribute("Orden", orden);
        return "usuario/carrito";
    }

    @GetMapping("/delete/cart/{id}")
    public String deleteProducto(@PathVariable Integer id, Model model) {
        // Lista nueva de productos
        List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();

        for (DetalleOrden detalleOrden : detalles) {
            if (detalleOrden.getProducto().getId() != id) {
                ordenesNueva.add(detalleOrden);
            }
            // nueva lista con los productos que no se eliminaron
            detalles = ordenesNueva;

            double sumaTotal = 0;

            sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

            orden.setTotal(sumaTotal);
            model.addAttribute("Cart", detalles);
            model.addAttribute("Orden", orden);

        }

        return "usuario/carrito";
    }

    @GetMapping("/getCart")
    public String getCart(Model model, HttpSession session) {
        model.addAttribute("Cart", detalles);
        model.addAttribute("Orden", orden);

        //sesion
        model.addAttribute("sesion", session.getAttribute("idusuario"));

        return "/usuario/carrito";
    }

    @GetMapping("/order")
    public String order(Model model, HttpSession session){
        
        Usuario usuario = usuarioService.findByid(Integer.valueOf(session.getAttribute("idusuario").toString())).get();

        model.addAttribute("Cart", detalles);
        model.addAttribute("Orden", orden);
        model.addAttribute("Usuario", usuario);

        return "usuario/resumenorden";
    }


    @GetMapping("/saveOrder")
    public String saveOrder(Model model, HttpSession session){
        Date fechaCreacion = new Date();
        orden.setFechaCreacion(fechaCreacion);
        orden.setNumero(ordenService.generarNumeroOrden());

        //usuario
        Usuario usuario = usuarioService.findByid(Integer.parseInt(session.getAttribute("idusuario").toString())).get();

        orden.setUsuario(usuario);
        ordenService.save(orden);

        //guardar detalles
        for(DetalleOrden dt: detalles){
            dt.setOrden(orden);
            detalleOrdenService.save(dt);
        }

        //limpiar lista y orden
        orden = new Orden();
        detalles.clear();

        return "redirect:/";
    }

    @PostMapping("/buscar")
    public String buscarProducto(@RequestParam String nombre,Model model){
        LOGGER.info("nombre del producto: {}", nombre);
        //obtiene todos los productos con findall, hace un stream hace un filter donde se usa una funcion anonima 'p' o de flecha trae el nombre del producto
        //y con el metodo contains se pregunta si los productos contienen alguna parte de la secuencia de caracteres 'nombre' no los coloca y los devuelve con el metodo collect como una lista 
        List<Producto> productos = productoService.findAll().stream().filter( p -> p.getNombre().contains(nombre)).collect(Collectors.toList());
        
        //revisar como colocar condicionales al predicado de stream().filter(->predicado<-)
        //productos.addAll(productoService.findAll().stream().filter(p -> p.getDescripcion().contains(nombre)).collect(Collectors.toList()));
        model.addAttribute("productos", productos);
        
        return "usuario/home";
    }

}
