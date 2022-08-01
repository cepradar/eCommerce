package com.example.demo.service;

import java.util.Optional;

import com.example.demo.model.Usuario;

public interface IUsuarioService {
    Optional<Usuario> findByid(Integer id);
    Usuario save (Usuario usuario);
    Optional<Usuario> findByEmail(String email);
}
