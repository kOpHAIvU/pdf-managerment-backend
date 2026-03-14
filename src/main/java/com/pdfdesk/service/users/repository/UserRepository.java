package com.pdfdesk.service.users.repository;

import com.pdfdesk.service.users.model.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class UserRepository {
    private final DynamoDbTable<User> table;

    public UserRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder()
                        .dynamoDbClient(dynamoDbClient)
                        .build();
        this.table = enhancedClient.table("users", TableSchema.fromBean(User.class));
    }

    public void save(User user) {
        table.putItem(user);
    }

    public User findById(String id) {
        return table.getItem(r -> r.key(k -> k.partitionValue(id)));
    }

    public void delete(String id) {
        table.deleteItem(r -> r.key(k -> k.partitionValue(id)));
    }
}