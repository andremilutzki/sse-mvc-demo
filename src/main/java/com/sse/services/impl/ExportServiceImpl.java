package com.sse.services.impl;

import static java.lang.Thread.sleep;
import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

import com.sse.enums.ExportStatus;
import com.sse.models.Export;
import com.sse.repositories.ExportRepository;
import com.sse.services.ExportService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class ExportServiceImpl implements ExportService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

  private final ExportRepository exportRepository;

  @Autowired
  public ExportServiceImpl(ExportRepository exportRepository) {
    this.exportRepository = exportRepository;
  }

  @Override
  public void createExport() {
    try {
      Export export = exportRepository.save(new Export("SOME_EXPORT", ExportStatus.EXPORT_IN_PROGRESS.name(),
          new Date()));
      sleep(10000); // DO SOMETHING
      export.setStatus(ExportStatus.EXPORT_DONE.name());
      export.setLastUpdated(new Date());
      exportRepository.save(export);
    } catch (Exception exception) {
      LOGGER.error("Failed to create Export...");
    }
  }

  @Override
  public void checkStatusOfExport(SseEmitter sseEmitter) throws EntityNotFoundException, IOException,
      InterruptedException {
    boolean exportIsDoneOrNotStartedYet = false;
    boolean exportInProgressAlreadySent = false;

    do {
      try {
        Export export = findLatestExport();

        // Sent event if export is in progress but only once
        if (!exportInProgressAlreadySent && export.getStatus().equals(ExportStatus.EXPORT_IN_PROGRESS.toString())) {
          sseEmitter.send(event().name(export.getStatus()).data(export)); // SENT EVENT
          exportInProgressAlreadySent = true;
        }

        // Sent event if export is done
        if (export.getStatus().equals(ExportStatus.EXPORT_DONE.toString())) {
          sseEmitter.send(event().name(export.getStatus()).data(export)); // SENT EVENT
          exportIsDoneOrNotStartedYet = true;
        }

        // Check in 2 seconds again
        sleep(2000);
      } catch (EntityNotFoundException entityNotFoundException) {
        exportIsDoneOrNotStartedYet = true;
        sseEmitter.send(event().name("NO_EXPORT_STARTED_YET").data(""));
      }

      // Loop if export is not done or ever started yet
    } while (!exportIsDoneOrNotStartedYet);
  }

  private Export findLatestExport() {
    Optional<Export> export = exportRepository.findFirstByOrderByLastUpdatedDesc();
    if (export.isPresent()) {
      return export.get();
    } else {
      throw new EntityNotFoundException();
    }
  }
}
