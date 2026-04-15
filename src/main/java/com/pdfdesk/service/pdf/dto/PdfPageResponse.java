package com.pdfdesk.service.pdf.dto;

import java.util.List;

public record PdfPageResponse(
    List<PdfResponse> items,
    int page,
    int size,
    long totalItems,
    int totalPages
) {
}
