package com.pdfdesk.service.document.dto;

import com.pdfdesk.service.document.model.Document;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DocumentConnection {
  List<DocumentEdge> edges;
  PageInfo pageInfo;

  @Value
  @Builder
  public static class DocumentEdge {
    String cursor;
    Document node;
  }

  @Value
  @Builder
  public static class PageInfo {
    String endCursor;
    boolean hasNextPage;
  }
}
