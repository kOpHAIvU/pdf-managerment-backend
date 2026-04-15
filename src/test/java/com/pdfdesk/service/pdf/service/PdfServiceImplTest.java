package com.pdfdesk.service.pdf.service;

import com.pdfdesk.service.common.ForbiddenException;
import com.pdfdesk.service.pdf.dto.PdfMetadataUpdateRequest;
import com.pdfdesk.service.pdf.dto.PdfPageResponse;
import com.pdfdesk.service.pdf.dto.PdfResponse;
import com.pdfdesk.service.pdf.entity.PdfEntity;
import com.pdfdesk.service.pdf.repository.PdfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {
  @Mock
  private PdfRepository pdfRepository;
  @Mock
  private FileStorageService fileStorageService;
  @InjectMocks
  private PdfServiceImpl pdfService;

  private MockMultipartFile validPdf;

  @BeforeEach
  void setUp() {
    validPdf = new MockMultipartFile("file", "sample.pdf", "application/pdf", "content".getBytes());
  }

  @Test
  void uploadShouldStoreAndReturnPdf() {
    when(fileStorageService.store(any(), anyString())).thenReturn("/tmp/stored.pdf");
    when(pdfRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    PdfResponse response = pdfService.upload("user-1", validPdf);

    assertThat(response.id()).isNotBlank();
    assertThat(response.filename()).isEqualTo("sample.pdf");
    assertThat(response.uploadedBy()).isEqualTo("user-1");
  }

  @Test
  void listShouldFilterBySearchAndPaginate() {
    PdfEntity one = new PdfEntity();
    one.setId("1");
    one.setFilename("contract.pdf");
    one.setUrl("/tmp/1.pdf");
    one.setSize(200);
    one.setCreatedAt("2026-01-01T10:00:00Z");
    one.setUploadedBy("user-1");

    PdfEntity two = new PdfEntity();
    two.setId("2");
    two.setFilename("notes.pdf");
    two.setUrl("/tmp/2.pdf");
    two.setSize(300);
    two.setCreatedAt("2026-01-02T10:00:00Z");
    two.setUploadedBy("user-1");

    when(pdfRepository.findByUser("user-1")).thenReturn(List.of(one, two));

    PdfPageResponse page = pdfService.list("user-1", 0, 1, "note");

    assertThat(page.items()).hasSize(1);
    assertThat(page.items().get(0).filename()).isEqualTo("notes.pdf");
    assertThat(page.totalItems()).isEqualTo(1);
  }

  @Test
  void updateShouldRejectWhenUserDoesNotOwnPdf() {
    PdfEntity entity = new PdfEntity();
    entity.setId("pdf-1");
    entity.setFilename("old.pdf");
    entity.setUrl("/tmp/old.pdf");
    entity.setSize(100);
    entity.setCreatedAt("2026-01-02T10:00:00Z");
    entity.setUploadedBy("another-user");
    when(pdfRepository.findById("pdf-1")).thenReturn(Optional.of(entity));

    assertThatThrownBy(() -> pdfService.updateMetadata("user-1", "pdf-1", new PdfMetadataUpdateRequest("new.pdf")))
        .isInstanceOf(ForbiddenException.class);
  }
}
