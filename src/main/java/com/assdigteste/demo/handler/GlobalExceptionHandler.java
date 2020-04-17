package com.assdigteste.demo.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String TAMANHO_EXCEDIDO =
      "Não foi possível enviar o documento. " + "Tamanho do documento excede o limite permitido.";

  // CommonsMultipartResolver
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public String handleError2(MaxUploadSizeExceededException e,
      RedirectAttributes redirectAttributes) {

    redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
    return TAMANHO_EXCEDIDO;

  }

}
