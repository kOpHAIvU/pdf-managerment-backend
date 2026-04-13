package com.pdfdesk.service.users.repository;

import com.pdfdesk.service.users.model.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class UserRepository {
  private final DynamoDbTable<User> table;

  public UserRepository(DynamoDbEnhancedClient enhancedClient) {
    this.table = enhancedClient.table("users", TableSchema.fromBean(User.class));
  }

  public void save(User user) {
    table.putItem(user);
  }

  public User findById(String id) {
    return table.getItem(Key.builder().partitionValue(id).build());
  }

  public User findByEmail(String email) {
    DynamoDbIndex<User> index = table.index("email-index");

    QueryConditional query = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(email).build()
    );

    return index.query(query)
            .stream()
            .findFirst()
            .flatMap(page -> page.items().stream().findFirst())
            .orElse(null);
  }
}