package com.pdfdesk.service.users.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@DynamoDbBean
public class User {
  private String id;
  private String email;
  private String name;
  private String createdAt;

  @DynamoDbPartitionKey
  public String getId() {
    return id;
  }
}