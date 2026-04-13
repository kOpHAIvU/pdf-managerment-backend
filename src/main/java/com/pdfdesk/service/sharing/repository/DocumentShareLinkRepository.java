package com.pdfdesk.service.sharing.repository;

import com.pdfdesk.service.sharing.model.DocumentShareLink;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@Repository
public class DocumentShareLinkRepository {
  private final DynamoDbTable<DocumentShareLink> table;

  public DocumentShareLinkRepository(DynamoDbEnhancedClient enhancedClient) {
    this.table = enhancedClient.table("document_share_links", TableSchema.fromBean(DocumentShareLink.class));
  }

  public void save(DocumentShareLink link) {
    table.putItem(link);
  }

  public List<DocumentShareLink> findByDocumentId(String documentId) {
    return table.query(r -> r.queryConditional(
            software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
                .keyEqualTo(Key.builder().partitionValue(documentId).build())))
        .stream()
        .flatMap(page -> page.items().stream())
        .toList();
  }
}
