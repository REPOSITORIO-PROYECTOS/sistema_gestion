package com.sistema.gestion.Models.Devs;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "errorLogs")
@Getter
@Setter
public class ErrorLog {
  @Id
  private String id;
  private String message;
  private String exceptionType;
  private String stackTrace;
  private String path;
  private String method;
  private String user;
  private String requestBody;
  private Map<String, String> headers;
  private String customMessage;
  private Map<String, String> queryParams;
  private boolean resolved = false;
  private LocalDateTime timestamp;
}