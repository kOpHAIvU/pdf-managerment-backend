package com.pdfdesk.service.pdf.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
public class PdfEntity {
  private String id;
  private String filename;
  private String url;
  private long size;
  private String createdAt;
  private String uploadedBy;

  @DynamoDbPartitionKey
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = {"uploaded-by-index"})
  public String getUploadedBy() {
    return uploadedBy;
  }

  public void setUploadedBy(String uploadedBy) {
    this.uploadedBy = uploadedBy;
  }
}
