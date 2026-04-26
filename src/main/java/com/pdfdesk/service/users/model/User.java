package com.pdfdesk.service.users.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@DynamoDbBean
public class User {
  private String id;
  private String email;
  private String fullName;
  private String passwordHash;
  private String provider;
  private Payment payment;
  private String createdAt;

  @DynamoDbPartitionKey
  public String getId() {
    return id;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "email-index")
  public String getEmail() {
    return email;
  }
}