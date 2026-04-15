package com.pdfdesk.service.pdf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdfdesk.service.pdf.dto.PdfMetadataUpdateRequest;
import com.pdfdesk.service.pdf.dto.PdfPageResponse;
import com.pdfdesk.service.pdf.dto.PdfResponse;
import com.pdfdesk.service.pdf.service.PdfService;
import com.pdfdesk.service.security.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PdfController.class)
@AutoConfigureMockMvc(addFilters = false)
class PdfControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PdfService pdfService;
  @MockBean
  private CurrentUserProvider currentUserProvider;

  @Test
  void uploadShouldReturnStandardApiResponse() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", "content".getBytes());
    PdfResponse response = new PdfResponse("id-1", "file.pdf", "/uploads/id-1.pdf", 7, Instant.now(), "user-1");

    when(currentUserProvider.getCurrentUserId()).thenReturn("user-1");
    when(pdfService.upload(eq("user-1"), any())).thenReturn(response);

    mockMvc.perform(multipart("/api/pdfs").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value("id-1"));
  }

  @Test
  void listShouldReturnPaginatedResponse() throws Exception {
    when(currentUserProvider.getCurrentUserId()).thenReturn("user-1");
    when(pdfService.list("user-1", 0, 10, null)).thenReturn(new PdfPageResponse(List.of(), 0, 10, 0, 0));

    mockMvc.perform(get("/api/pdfs?page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.items").isArray());
  }

  @Test
  void patchShouldValidateBody() throws Exception {
    when(currentUserProvider.getCurrentUserId()).thenReturn("user-1");

    mockMvc.perform(patch("/api/pdfs/123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PdfMetadataUpdateRequest(""))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  void deleteShouldReturnSuccessResponse() throws Exception {
    when(currentUserProvider.getCurrentUserId()).thenReturn("user-1");

    mockMvc.perform(delete("/api/pdfs/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }
}
