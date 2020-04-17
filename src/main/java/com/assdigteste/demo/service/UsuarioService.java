package com.assdigteste.demo.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.assdigteste.demo.domain.Usuario;
import com.assdigteste.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {

  @Autowired
  UsuarioRepository repositorio;

  public Iterable<Usuario> getUsuarios() {
    return repositorio.findAll();
  }

  public Optional<Usuario> getUsuarioById(Long id) {
    return repositorio.findById(id);
  }

  public Usuario insert(Usuario usuario) {
    Assert.isNull(usuario.getId(), "Não foi possível inserir o registro");

    return repositorio.save(usuario);
  }

}
