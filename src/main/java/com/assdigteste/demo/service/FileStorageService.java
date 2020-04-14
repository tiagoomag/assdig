package com.assdigteste.demo.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

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

    private static final List<String> contentTypes = Arrays.asList("application/pdf", "application/msword",
	    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/excel",
	    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
	this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

	try {
	    Files.createDirectories(this.fileStorageLocation);
	} catch (Exception ex) {
	    throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
		    ex);
	}
    }

    public String storeFile(MultipartFile file) {
	// Normalize file name
	String fileName = StringUtils.cleanPath(file.getOriginalFilename());

	// Tipo do arquivo
	String fileContentType = file.getContentType();

	try {
	    /* Verifica se a extensão do arquivo é válida */
	    if (contentTypes.contains(fileContentType)) {

		// Check if the file's name contains invalid characters
		if (fileName.contains("..")) {
		    throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
		}

		// Copy file to the target location (Replacing existing file with the same name)
		Path targetLocation = this.fileStorageLocation.resolve(fileName);
		Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

		return fileName;
	    } else {
		throw new FileStorageException("Tipo de arquivo inválido " + fileName);
	    }
	} catch (IOException ex) {
	    throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
	}
    }

    public Resource loadFileAsResource(String fileName) {
	try {
	    Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
	    Resource resource = new UrlResource(filePath.toUri());
	    if (resource.exists()) {
		return resource;
	    } else {
		throw new MyFileNotFoundException("File not found " + fileName);
	    }
	} catch (MalformedURLException ex) {
	    throw new MyFileNotFoundException("File not found " + fileName, ex);
	}
    }
}
