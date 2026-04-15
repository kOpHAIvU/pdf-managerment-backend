package com.pdfdesk.service.pdf.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
  String store(MultipartFile file, String uniqueName);

  void delete(String storedPath);
}
