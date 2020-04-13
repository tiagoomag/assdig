package com.assdigteste.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.assdigteste.demo.domain.Documento;
import com.assdigteste.demo.repository.DocumentoRepository;

@Service
public class DocumentoService {

    @Autowired
    DocumentoRepository repositorio;

    public Iterable<Documento> getDocumentos() {
	return repositorio.findAll();
    }

    public Documento insert(Documento documento) {
	Assert.isNull(documento.getId(), "Não foi possível inserir o registro");

	return repositorio.save(documento);
    }

}
