package com.pdfdesk.service.users.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@Data
@DynamoDbBean
public class Payment {
  private PaymentSubscription subscription;
  private List<PaymentTransaction> transactions;
  private String preferredMethod;
}
