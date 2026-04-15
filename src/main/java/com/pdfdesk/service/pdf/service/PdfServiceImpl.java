package com.pdfdesk.service.pdf.service;

import com.pdfdesk.service.common.ForbiddenException;
import com.pdfdesk.service.common.NotFoundException;
import com.pdfdesk.service.pdf.dto.PdfMetadataUpdateRequest;
import com.pdfdesk.service.pdf.dto.PdfPageResponse;
import com.pdfdesk.service.pdf.dto.PdfResponse;
import com.pdfdesk.service.pdf.entity.PdfEntity;
import com.pdfdesk.service.pdf.repository.PdfRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class PdfServiceImpl implements PdfService {
  private static final long MAX_PDF_BYTES = 10 * 1024 * 1024;
  private static final String PDF_CONTENT_TYPE = "application/pdf";

  private final PdfRepository pdfRepository;
  private final FileStorageService fileStorageService;

  public PdfServiceImpl(PdfRepository pdfRepository, FileStorageService fileStorageService) {
    this.pdfRepository = pdfRepository;
    this.fileStorageService = fileStorageService;
  }

  @Override
  public PdfResponse upload(String userId, MultipartFile file) {
    validatePdf(file);
    String uniqueName = UUID.randomUUID() + ".pdf";
    String storagePath = fileStorageService.store(file, uniqueName);

    PdfEntity entity = new PdfEntity();
    entity.setId(UUID.randomUUID().toString());
    entity.setFilename(file.getOriginalFilename() == null ? uniqueName : file.getOriginalFilename());
    entity.setUrl(storagePath);
    entity.setSize(file.getSize());
    entity.setCreatedAt(Instant.now().toString());
    entity.setUploadedBy(userId);

    pdfRepository.save(entity);
    log.info("pdf_upload_success userId={} pdfId={} size={}", userId, entity.getId(), entity.getSize());
    return toResponse(entity);
  }

  @Override
  public PdfPageResponse list(String userId, int page, int size, String search) {
    int safePage = Math.max(page, 0);
    int safeSize = Math.max(1, Math.min(size, 100));
    String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);

    List<PdfResponse> filtered = pdfRepository.findByUser(userId).stream()
        .filter(entity -> normalizedSearch.isBlank() || entity.getFilename().toLowerCase(Locale.ROOT).contains(normalizedSearch))
        .sorted(Comparator.comparing(PdfEntity::getCreatedAt).reversed())
        .map(this::toResponse)
        .toList();

    int from = Math.min(safePage * safeSize, filtered.size());
    int to = Math.min(from + safeSize, filtered.size());
    int totalPages = filtered.isEmpty() ? 0 : (int) Math.ceil((double) filtered.size() / safeSize);

    return new PdfPageResponse(filtered.subList(from, to), safePage, safeSize, filtered.size(), totalPages);
  }

  @Override
  public PdfResponse updateMetadata(String userId, String pdfId, PdfMetadataUpdateRequest request) {
    PdfEntity entity = requireOwned(userId, pdfId);
    entity.setFilename(request.filename());
    pdfRepository.save(entity);
    log.info("pdf_metadata_updated userId={} pdfId={}", userId, pdfId);
    return toResponse(entity);
  }

  @Override
  public void delete(String userId, String pdfId) {
    PdfEntity entity = requireOwned(userId, pdfId);
    pdfRepository.delete(entity.getId());
    fileStorageService.delete(entity.getUrl());
    log.info("pdf_deleted userId={} pdfId={}", userId, pdfId);
  }

  private PdfEntity requireOwned(String userId, String pdfId) {
    PdfEntity entity = pdfRepository.findById(pdfId)
        .orElseThrow(() -> new NotFoundException("PDF not found"));
    if (!userId.equals(entity.getUploadedBy())) {
      throw new ForbiddenException("Not allowed to access this PDF");
    }
    return entity;
  }

  private void validatePdf(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("PDF file is required");
    }
    if (file.getSize() > MAX_PDF_BYTES) {
      throw new IllegalArgumentException("PDF exceeds maximum allowed size (10 MB)");
    }
    String contentType = file.getContentType();
    String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
    if (!PDF_CONTENT_TYPE.equalsIgnoreCase(contentType) && !filename.endsWith(".pdf")) {
      throw new IllegalArgumentException("Only PDF files are allowed");
    }
  }

  private PdfResponse toResponse(PdfEntity entity) {
    return new PdfResponse(
        entity.getId(),
        entity.getFilename(),
        entity.getUrl(),
        entity.getSize(),
        Instant.parse(entity.getCreatedAt()),
        entity.getUploadedBy()
    );
  }
}
