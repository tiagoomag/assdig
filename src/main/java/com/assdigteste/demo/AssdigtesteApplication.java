package com.assdigteste.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.assdigteste.demo.property.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class AssdigtesteApplication {

  public static void main(String[] args) {
    SpringApplication.run(AssdigtesteApplication.class, args);
  }

}
