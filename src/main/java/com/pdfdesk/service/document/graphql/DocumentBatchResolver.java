package com.pdfdesk.service.document.graphql;

import com.pdfdesk.service.document.model.Document;
import com.pdfdesk.service.users.model.User;
import com.pdfdesk.service.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DocumentBatchResolver {
  private final UserRepository userRepository;

  @BatchMapping(typeName = "Document", field = "owner")
  public Map<Document, User> owner(List<Document> documents) {
    Map<Document, User> result = new LinkedHashMap<>();
    for (Document document : documents) {
      result.put(document, userRepository.findById(document.getOwnerUserId()));
    }
    return result;
  }
}
