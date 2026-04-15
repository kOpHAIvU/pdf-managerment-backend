package com.pdfdesk.service.pdf.service;

import com.pdfdesk.service.pdf.dto.PdfMetadataUpdateRequest;
import com.pdfdesk.service.pdf.dto.PdfPageResponse;
import com.pdfdesk.service.pdf.dto.PdfResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PdfService {
  PdfResponse upload(String userId, MultipartFile file);

  PdfPageResponse list(String userId, int page, int size, String search);

  PdfResponse updateMetadata(String userId, String pdfId, PdfMetadataUpdateRequest request);

  void delete(String userId, String pdfId);
}
