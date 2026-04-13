package com.pdfdesk.service.sharing.model;

import com.pdfdesk.service.permission.model.DocumentRole;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
public class DocumentShareLink {
  private String documentId;
  private String tokenHash;
  private DocumentRole role;
  private String expiresAt;
  private String createdBy;
  private String createdAt;

  @DynamoDbPartitionKey
  public String getDocumentId() {
    return documentId;
  }

  @DynamoDbSortKey
  public String getTokenHash() {
    return tokenHash;
  }
}
