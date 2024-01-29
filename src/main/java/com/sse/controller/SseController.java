package com.sse.controller;

import com.sse.services.ExportService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@CrossOrigin
@RestController
public class SseController {
  private final ExecutorService threadPool = Executors.newCachedThreadPool();
  private final ExportService exportService;

  @Autowired
  public SseController(ExportService exportService) {
    this.exportService = exportService;
  }

  @PostMapping("/exports")
  public ResponseEntity<String> createExport() {
    this.exportService.createExport();
    return ResponseEntity.ok().build();
  }

  @GetMapping("/stream-events")
  public SseEmitter streamExportStatusUpdates() {
    SseEmitter emitter = new SseEmitter();
    threadPool.execute(() -> {
      try {
        exportService.checkStatusOfExport(emitter);
        emitter.complete();
      } catch (Exception ex) {
        emitter.completeWithError(ex);
      }
    });
    return emitter;
  }
}
