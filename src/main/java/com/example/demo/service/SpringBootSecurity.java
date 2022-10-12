package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringBootSecurity extends WebSecurityConfigurerAdapter{

    @Autowired
    private UserDetailsService userDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(getEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //verifica que el usuario sea Admin para darle acceso a las url en cuestion
        http.csrf().disable().authorizeRequests()
        .antMatchers("/administrador/**").hasRole("ADMIN")
        .antMatchers("/productos/**").hasRole("ADMIN")
        .and().formLogin().loginPage("/usuario/login")
        //permitir accesos a los demas usuario al resto de url
        .permitAll().defaultSuccessUrl("/usuario/acceder");
    }

    @Bean
    public BCryptPasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    }    
}
