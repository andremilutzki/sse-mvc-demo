package com.sse.services;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ExportService {
  @Async
  void createExport();

  void checkStatusOfExport(SseEmitter sseEmitter) throws EntityNotFoundException, IOException, InterruptedException;
}
