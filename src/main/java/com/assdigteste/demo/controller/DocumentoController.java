package com.assdigteste.demo.controller;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.assdigteste.demo.domain.Documento;
import com.assdigteste.demo.domain.Usuario;
import com.assdigteste.demo.payload.UploadFileResponse;
import com.assdigteste.demo.service.DocumentoService;
import com.assdigteste.demo.service.FileStorageService;
import com.assdigteste.demo.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/documentos")
public class DocumentoController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentoController.class);

    @Autowired
    DocumentoService documentoService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping()
    public ResponseEntity get() {
	return ResponseEntity.ok(documentoService.getDocumentos());
    }

    private URI getUri(Long id) {
	return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }

    @PostMapping
    public ResponseEntity post(@RequestBody Documento documento) {

	Documento d = documentoService.insert(documento);

	URI location = getUri(d.getId());
	return ResponseEntity.created(location).build();
    }

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, Documento documento,
	    Usuario usuario) {
	String fileName = fileStorageService.storeFile(file);

	String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
		.path("/api/v1/documentos/downloadFile/").path(fileName).toUriString();

	/* recuperando apenas a extensão do arquivo */
	String extensao = fileName.substring(fileName.lastIndexOf(".") + 1);

	/* recuperando apenas nome do arquivo */
	String nome = fileName.substring(0, fileName.indexOf('.'));

	Long usuarioLogado = 1L;
	Optional<Usuario> usuarioEncontrado = usuarioService.getUsuarioById(usuarioLogado);

	documento.setUsuario(usuarioEncontrado.get());
	documento.setExtensao(extensao);
	documento.setNome(nome);
	documento.setTamanho(file.getSize());
	documento.setTipo(file.getContentType());
	documentoService.insert(documento);

	return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
	// Load file as Resource
	Resource resource = fileStorageService.loadFileAsResource(fileName);

	// Try to determine file's content type
	String contentType = null;
	try {
	    contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
	} catch (IOException ex) {
	    logger.info("Could not determine file type.");
	}

	// Fallback to the default content type if type could not be determined
	if (contentType == null) {
	    contentType = "application/octet-stream";
	}

	return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
		.body(resource);
    }

}
