package com.assdigteste.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assdigteste.demo.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
