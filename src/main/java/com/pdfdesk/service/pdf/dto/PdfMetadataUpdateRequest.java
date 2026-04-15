package com.pdfdesk.service.pdf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PdfMetadataUpdateRequest(
    @NotBlank(message = "filename is required")
    @Size(min = 1, max = 255, message = "filename must be between 1 and 255 characters")
    String filename
) {
}
