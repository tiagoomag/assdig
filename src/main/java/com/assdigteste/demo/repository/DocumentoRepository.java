package com.assdigteste.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assdigteste.demo.domain.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

}
