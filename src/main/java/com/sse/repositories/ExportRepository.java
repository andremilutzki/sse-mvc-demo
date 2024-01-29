package com.sse.repositories;

import com.sse.models.Export;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportRepository extends CrudRepository<Export, Long> {
  Optional<Export> findFirstByOrderByLastUpdatedDesc();
}
