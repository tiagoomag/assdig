package com.assdigteste.demo.controller;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.assdigteste.demo.domain.Usuario;
import com.assdigteste.demo.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsusarioController {

  @Autowired
  UsuarioService service;

  @GetMapping()
  public ResponseEntity get() {
    return ResponseEntity.ok(service.getUsuarios());
  }

  @GetMapping("/{id}")
  public ResponseEntity getById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(service.getUsuarioById(id));
  }

  private URI getUri(Long id) {
    return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id)
        .toUri();
  }

  @PostMapping
  public ResponseEntity post(@RequestBody Usuario usuario) {

    Usuario u = service.insert(usuario);

    URI location = getUri(u.getId());
    return ResponseEntity.created(location).build();
  }

}
