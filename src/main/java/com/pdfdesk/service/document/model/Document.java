package com.pdfdesk.service.document.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@Data
@DynamoDbBean
public class Document {
  private String id;
  private String ownerUserId;
  private String title;
  private String storageKey;
  private String mimeType;
  private Long size;
  private DocumentVisibility visibility;
  private String createdAt;
  private String updatedAt;

  @DynamoDbPartitionKey
  public String getId() {
    return id;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "owner-index")
  public String getOwnerUserId() {
    return ownerUserId;
  }
}
