package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Orden;

public interface IOrdenService {
    Orden save (Orden orden);
    List<Orden> findAll();
    String generarNumeroOrden();
}
