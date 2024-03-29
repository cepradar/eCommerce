package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Orden;
import com.example.demo.model.Usuario;

public interface IOrdenService {
    Orden save (Orden orden);
    List<Orden> findAll();
    String generarNumeroOrden();
    List<Orden> findByUsuario(Usuario usuario);
    Optional<Orden> findById(Integer id);
}
