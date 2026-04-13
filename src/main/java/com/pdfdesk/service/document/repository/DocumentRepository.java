package com.pdfdesk.service.document.repository;

import com.pdfdesk.service.document.model.Document;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
public class DocumentRepository {
  private final DynamoDbTable<Document> table;

  public DocumentRepository(DynamoDbEnhancedClient enhancedClient) {
    this.table = enhancedClient.table("documents", TableSchema.fromBean(Document.class));
  }

  public void save(Document document) {
    table.putItem(document);
  }

  public Document findById(String id) {
    return table.getItem(Key.builder().partitionValue(id).build());
  }

  public List<Document> findByOwner(String ownerUserId, int limit) {
    DynamoDbIndex<Document> ownerIndex = table.index("owner-index");
    QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(ownerUserId).build());
    return ownerIndex.query(r -> r.queryConditional(query).limit(limit))
        .stream()
        .flatMap(page -> page.items().stream())
        .toList();
  }
}
