package com.pdfdesk.service.audit.repository;

import com.pdfdesk.service.audit.model.AuditLogEvent;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class AuditLogRepository {
  private final DynamoDbTable<AuditLogEvent> table;

  public AuditLogRepository(DynamoDbEnhancedClient enhancedClient) {
    this.table = enhancedClient.table("audit_events", TableSchema.fromBean(AuditLogEvent.class));
  }

  public void save(AuditLogEvent event) {
    table.putItem(event);
  }
}
