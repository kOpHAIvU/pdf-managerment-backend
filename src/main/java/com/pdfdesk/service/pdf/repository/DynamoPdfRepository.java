package com.pdfdesk.service.pdf.repository;

import com.pdfdesk.service.pdf.entity.PdfEntity;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@Repository
public class DynamoPdfRepository implements PdfRepository {
  private final DynamoDbTable<PdfEntity> table;

  public DynamoPdfRepository(DynamoDbEnhancedClient enhancedClient) {
    this.table = enhancedClient.table("pdf_files", TableSchema.fromBean(PdfEntity.class));
  }

  @Override
  public PdfEntity save(PdfEntity pdfEntity) {
    table.putItem(pdfEntity);
    return pdfEntity;
  }

  @Override
  public Optional<PdfEntity> findById(String id) {
    return Optional.ofNullable(table.getItem(Key.builder().partitionValue(id).build()));
  }

  @Override
  public List<PdfEntity> findByUser(String userId) {
    DynamoDbIndex<PdfEntity> userIndex = table.index("uploaded-by-index");
    QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());
    return userIndex.query(r -> r.queryConditional(query))
        .stream()
        .flatMap(page -> page.items().stream())
        .toList();
  }

  @Override
  public void delete(String id) {
    table.deleteItem(Key.builder().partitionValue(id).build());
  }
}
