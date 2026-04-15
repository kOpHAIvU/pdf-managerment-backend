package com.pdfdesk.service.pdf.controller;

import com.pdfdesk.service.common.api.ApiResponse;
import com.pdfdesk.service.pdf.dto.PdfMetadataUpdateRequest;
import com.pdfdesk.service.pdf.dto.PdfPageResponse;
import com.pdfdesk.service.pdf.dto.PdfResponse;
import com.pdfdesk.service.pdf.service.PdfService;
import com.pdfdesk.service.security.CurrentUserProvider;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdfs")
public class PdfController {
  private final PdfService pdfService;
  private final CurrentUserProvider currentUserProvider;

  public PdfController(PdfService pdfService, CurrentUserProvider currentUserProvider) {
    this.pdfService = pdfService;
    this.currentUserProvider = currentUserProvider;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<PdfResponse> upload(@RequestPart("file") MultipartFile file) {
    String userId = requireUserId();
    return ApiResponse.success("PDF uploaded successfully", pdfService.upload(userId, file));
  }

  @GetMapping
  public ApiResponse<PdfPageResponse> list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String search
  ) {
    String userId = requireUserId();
    return ApiResponse.success("PDF list retrieved", pdfService.list(userId, page, size, search));
  }

  @PatchMapping("/{pdfId}")
  public ApiResponse<PdfResponse> updateMetadata(
      @PathVariable String pdfId,
      @Valid @RequestBody PdfMetadataUpdateRequest request
  ) {
    String userId = requireUserId();
    return ApiResponse.success("PDF metadata updated", pdfService.updateMetadata(userId, pdfId, request));
  }

  @DeleteMapping("/{pdfId}")
  public ApiResponse<Void> delete(@PathVariable String pdfId) {
    String userId = requireUserId();
    pdfService.delete(userId, pdfId);
    return ApiResponse.success("PDF deleted successfully");
  }

  private String requireUserId() {
    String userId = currentUserProvider.getCurrentUserId();
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("Unauthenticated request");
    }
    return userId;
  }
}
