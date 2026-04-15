package com.pdfdesk.service.pdf.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalFileStorageService implements FileStorageService {
  private final Path storageDirectory;

  public LocalFileStorageService(@Value("${storage.local.upload-dir:uploads}") String uploadDir) {
    this.storageDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(storageDirectory);
    } catch (IOException e) {
      throw new IllegalStateException("Could not initialize upload directory", e);
    }
  }

  @Override
  public String store(MultipartFile file, String uniqueName) {
    try {
      Path destination = storageDirectory.resolve(uniqueName);
      Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
      return destination.toString();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to store PDF file", e);
    }
  }

  @Override
  public void delete(String storedPath) {
    if (storedPath == null || storedPath.isBlank()) {
      return;
    }
    try {
      Files.deleteIfExists(Paths.get(storedPath));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to delete stored file", e);
    }
  }
}
