package com.pdfdesk.service.document.service;

import com.pdfdesk.service.common.NotFoundException;
import com.pdfdesk.service.document.dto.DocumentConnection;
import com.pdfdesk.service.document.model.Document;
import com.pdfdesk.service.document.model.DocumentVisibility;
import com.pdfdesk.service.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
  private final DocumentRepository documentRepository;

  public Document create(String ownerUserId, String title, DocumentVisibility visibility) {
    String now = Instant.now().toString();
    Document document = new Document();
    document.setId(UUID.randomUUID().toString());
    document.setOwnerUserId(ownerUserId);
    document.setTitle(title);
    document.setVisibility(visibility);
    document.setCreatedAt(now);
    document.setUpdatedAt(now);
    documentRepository.save(document);
    return document;
  }

  public Document getById(String id) {
    Document document = documentRepository.findById(id);
    if (document == null) {
      throw new NotFoundException("Document not found");
    }
    return document;
  }

  public DocumentConnection listByOwner(String ownerUserId, Integer first, String after) {
    int pageSize = first == null || first <= 0 ? 20 : Math.min(first, 100);
    List<Document> sorted = documentRepository.findByOwner(ownerUserId, 200)
        .stream()
        .sorted(Comparator.comparing(Document::getUpdatedAt).reversed())
        .toList();

    int startIndex = decodeCursor(after);
    int endIndex = Math.min(startIndex + pageSize, sorted.size());
    List<DocumentConnection.DocumentEdge> edges = sorted.subList(startIndex, endIndex).stream()
        .map(document -> DocumentConnection.DocumentEdge.builder()
            .cursor(encodeCursor(sorted.indexOf(document) + 1))
            .node(document)
            .build())
        .toList();

    return DocumentConnection.builder()
        .edges(edges)
        .pageInfo(DocumentConnection.PageInfo.builder()
            .endCursor(edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor())
            .hasNextPage(endIndex < sorted.size())
            .build())
        .build();
  }

  private int decodeCursor(String after) {
    if (after == null || after.isBlank()) {
      return 0;
    }
    try {
      return Integer.parseInt(after);
    } catch (NumberFormatException ignored) {
      return 0;
    }
  }

  private String encodeCursor(int index) {
    return String.valueOf(index);
  }
}
