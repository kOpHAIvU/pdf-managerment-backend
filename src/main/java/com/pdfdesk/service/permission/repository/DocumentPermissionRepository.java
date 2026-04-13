package com.pdfdesk.service.permission.repository;

import com.pdfdesk.service.permission.model.DocumentPermissionGrant;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
public class DocumentPermissionRepository {
  private final DynamoDbTable<DocumentPermissionGrant> table;

  public DocumentPermissionRepository(DynamoDbEnhancedClient enhancedClient) {
    this.table = enhancedClient.table("document_acl", TableSchema.fromBean(DocumentPermissionGrant.class));
  }

  public void upsert(DocumentPermissionGrant grant) {
    table.putItem(grant);
  }

  public List<DocumentPermissionGrant> findByDocumentId(String documentId) {
    return table.query(r -> r.queryConditional(
            QueryConditional.keyEqualTo(Key.builder().partitionValue(documentId).build())))
        .stream()
        .flatMap(page -> page.items().stream())
        .toList();
  }
}
