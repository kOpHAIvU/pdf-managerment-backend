package com.pdfdesk.service.pdf.dto;

import java.time.Instant;

public record PdfResponse(
    String id,
    String filename,
    String url,
    long size,
    Instant createdAt,
    String uploadedBy
) {
}
