package com.pdfdesk.service.pdf.repository;

import com.pdfdesk.service.pdf.entity.PdfEntity;

import java.util.List;
import java.util.Optional;

public interface PdfRepository {
  PdfEntity save(PdfEntity pdfEntity);

  Optional<PdfEntity> findById(String id);

  List<PdfEntity> findByUser(String userId);

  void delete(String id);
}
