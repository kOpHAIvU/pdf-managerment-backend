package com.pdfdesk.service.users.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class PaymentTransaction {
  private String transactionId;
  private String method;
  private String tier;
  private Long amount;
  private String currency;
  private String status;
  private String createdAt;
  private String paidAt;
  private String rawResponse;
}
