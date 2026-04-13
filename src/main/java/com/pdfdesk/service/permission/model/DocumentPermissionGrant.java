package com.pdfdesk.service.permission.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
public class DocumentPermissionGrant {
  private String documentId;
  private String principal;
  private DocumentRole role;
  private String grantedBy;
  private String grantedAt;

  @DynamoDbPartitionKey
  public String getDocumentId() {
    return documentId;
  }

  @DynamoDbSortKey
  public String getPrincipal() {
    return principal;
  }
}
