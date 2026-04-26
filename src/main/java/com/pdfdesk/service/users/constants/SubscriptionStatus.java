package com.pdfdesk.service.users.constants;

public enum SubscriptionTier {
  FREE, STARTER, PRO
}

public enum SubscriptionStatus {
  ACTIVE, EXPIRED, CANCELLED, PENDING
}

public enum PaymentMethod {
  VNPAY, ZALOPAY
}

public enum PaymentTransactionStatus {
  PENDING, SUCCESS, FAILED, REFUNDED
}
