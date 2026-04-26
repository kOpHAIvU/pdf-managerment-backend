package com.pdfdesk.service.users.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class PaymentSubscription {
  private String tier;
  private String status;
  private String startDate;
  private String endDate;
  private Boolean autoRenew;
}
