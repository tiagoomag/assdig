package com.assdigteste.demo.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.assdigteste.demo.exception.FileStorageException;
import com.assdigteste.demo.exception.MyFileNotFoundException;
import com.assdigteste.demo.property.FileStorageProperties;

@Service
public class FileStorageService {

    private static final String TIPO_ARQUIVO = "Tipo de arquivo inválido.";
    private static final String FALHA_ARMAZENAMENTO = "Não foi possível armazenar o arquivo.";
    private static final String SEQUENCIA_INVALIDA = "O nome do arquivo contém uma sequência de caminhos inválida.";
    private static final String NAO_ENCONTRADO = "Arquivo não encontrado. ";
    private static final String DIRETORIO = "Não foi possível criar o diretório em que os arquivos enviados serão armazenados.";

    private static final String TIPO_CONTEUDO = "application/pdf";

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
	this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

	try {
	    Files.createDirectories(this.fileStorageLocation);
	} catch (Exception ex) {
	    throw new FileStorageException(DIRETORIO, ex);
	}
    }

    public String storeFile(MultipartFile file) {
	// Normalize file name
	String fileName = StringUtils.cleanPath(file.getOriginalFilename());

	// Tipo do arquivo
	String fileContentType = file.getContentType();

	try {
	    /* Verifica se a extensão/tipo do arquivo é válida (pdf) */
	    if (!TIPO_CONTEUDO.contains(fileContentType)) {
		throw new FileStorageException(TIPO_ARQUIVO);
	    }
	    // Check if the file's name contains invalid characters
	    if (fileName.contains("..")) {
		throw new FileStorageException(SEQUENCIA_INVALIDA + fileName);
	    }

	    // Copy file to the target location (Replacing existing file with the same name)
	    Path targetLocation = this.fileStorageLocation.resolve(fileName);
	    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

	    return fileName;
	} catch (IOException ex) {
	    throw new FileStorageException(FALHA_ARMAZENAMENTO, ex);
	}
    }

    public Resource loadFileAsResource(String fileName) {
	try {
	    Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
	    Resource resource = new UrlResource(filePath.toUri());
	    if (resource.exists()) {
		return resource;
	    } else {
		throw new MyFileNotFoundException(NAO_ENCONTRADO + fileName);
	    }
	} catch (MalformedURLException ex) {
	    throw new MyFileNotFoundException(NAO_ENCONTRADO + fileName, ex);
	}
    }
}
