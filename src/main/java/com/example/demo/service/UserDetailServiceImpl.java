package com.example.demo.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.Usuario;

import jakarta.servlet.http.HttpSession;

@Service
public class UserDetailServiceImpl implements UserDetailsService{

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    HttpSession session;

    private Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Este es el username");
        log.info("hasta aqui vamos bn");
        Optional<Usuario> optionalUser = usuarioService.findByid(1);
        if(optionalUser.isPresent()){
            log.info("Este es el id del usuario:{}", optionalUser.get().getId());
            //log.info("Este es el idUsuario antes: {}", Integer.parseInt(session.getAttribute("idUsuario").toString()));
            session.setAttribute("idUsuario", optionalUser.get().getId());
            log.info("Este es el idUsuario despues: {}", Integer.parseInt(session.getAttribute("idUsuario").toString()));
            Usuario usuario = optionalUser.get();
            //return que genera el usuario y hace el match de la contraseña encriptada
            return User.builder().username(usuario.getNombre()).password(usuario.getPsw()).roles(usuario.getTipo()).build();
        }else{
            throw new UsernameNotFoundException("usuario no encontrado");
        }
    }
    
}
