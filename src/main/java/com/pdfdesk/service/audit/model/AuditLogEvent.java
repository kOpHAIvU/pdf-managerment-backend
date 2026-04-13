package com.pdfdesk.service.audit.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
public class AuditLogEvent {
  private String documentId;
  private String eventId;
  private String actorUserId;
  private String action;
  private String metadata;
  private String createdAt;

  @DynamoDbPartitionKey
  public String getDocumentId() {
    return documentId;
  }

  @DynamoDbSortKey
  public String getEventId() {
    return eventId;
  }
}
