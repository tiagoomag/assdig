package com.assdigteste.demo.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.assdigteste.demo.exception.FileAlreadyExistsException;
import com.assdigteste.demo.exception.FileStorageException;
import com.assdigteste.demo.exception.MyFileNotFoundException;
import com.assdigteste.demo.property.FileStorageProperties;

@Service
public class FileStorageService {

  private static final String DOCUMENTO_INVALIDO =
      "Tipo de arquivo inválido. Verifique o formato do arquivo.";
  private static final String FALHA_ARMAZENAMENTO = "Não foi possível armazenar o arquivo.";
  private static final String NAO_ENCONTRADO = "Documento não encontrado. ";
  private static final String DIRETORIO =
      "Não foi possível criar o diretório em que os arquivos enviados serão " + "armazenados.";
  private static final String DOCUMENTO_EXISTE = "Já existe um documento com  este nome. ";
  private static final String TIPO_CONTEUDO = "application/pdf";

  private final Path fileStorageLocation;

  @Autowired
  public FileStorageService(FileStorageProperties fileStorageProperties) {
    this.fileStorageLocation =
        Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

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

      /* Verifica se já existe um arquivo com o mesmo nome + extensão */
      checkDocumentExists(fileName);

      /* Verifica se o arquivo é válido */
      if (!(TIPO_CONTEUDO.contains(fileContentType)) || fileName.contains("..")) {
        throw new FileStorageException(DOCUMENTO_INVALIDO);
      }

      /* Copia arquivo para o local */
      Path targetLocation = this.fileStorageLocation.resolve(fileName);
      // Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      Files.copy(file.getInputStream(), targetLocation);

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

  public void checkDocumentExists(String fileNameServer) {
    try {
      Path filePath = this.fileStorageLocation.resolve(fileNameServer).normalize();
      Resource resource = new UrlResource(filePath.toUri());

      if (resource.exists()) {
        throw new FileAlreadyExistsException(DOCUMENTO_EXISTE + fileNameServer);
      }

    } catch (IOException ex) {
      throw new FileStorageException(FALHA_ARMAZENAMENTO + fileNameServer);
    }
  }

}
