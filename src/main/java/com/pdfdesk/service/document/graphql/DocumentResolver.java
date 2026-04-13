package com.pdfdesk.service.document.graphql;

import com.pdfdesk.service.audit.service.AuditLogService;
import com.pdfdesk.service.document.dto.DocumentConnection;
import com.pdfdesk.service.document.model.Document;
import com.pdfdesk.service.document.model.DocumentVisibility;
import com.pdfdesk.service.document.service.DocumentService;
import com.pdfdesk.service.permission.model.DocumentRole;
import com.pdfdesk.service.permission.service.PermissionService;
import com.pdfdesk.service.security.CurrentUserProvider;
import com.pdfdesk.service.sharing.service.SharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DocumentResolver {
  private final DocumentService documentService;
  private final PermissionService permissionService;
  private final SharingService sharingService;
  private final AuditLogService auditLogService;
  private final CurrentUserProvider currentUserProvider;

  @QueryMapping
  public Document document(@Argument String id) {
    String userId = requireUserId();
    Document document = documentService.getById(id);
    if (document.getVisibility() != DocumentVisibility.PUBLIC) {
      permissionService.requireCanRead(userId, document);
    }
    auditLogService.record(id, userId, "READ_DOCUMENT", Map.of("documentId", id));
    return document;
  }

  @QueryMapping
  public DocumentConnection documents(@Argument Integer first, @Argument String after) {
    return documentService.listByOwner(requireUserId(), first, after);
  }

  @MutationMapping
  public Document createDocument(@Argument String title, @Argument DocumentVisibility visibility) {
    Document created = documentService.create(requireUserId(), title, visibility == null ? DocumentVisibility.PRIVATE : visibility);
    auditLogService.record(created.getId(), created.getOwnerUserId(), "CREATE_DOCUMENT", Map.of("title", created.getTitle()));
    return created;
  }

  @MutationMapping
  public boolean shareWithUser(@Argument String documentId, @Argument String email, @Argument DocumentRole role) {
    return sharingService.shareWithUser(requireUserId(), documentId, email, role == null ? DocumentRole.VIEWER : role);
  }

  @MutationMapping
  public String createLinkShare(
      @Argument String documentId,
      @Argument DocumentRole role,
      @Argument String expiresAt
  ) {
    return sharingService.createLinkShare(
        requireUserId(),
        documentId,
        role == null ? DocumentRole.VIEWER : role,
        expiresAt
    );
  }

  private String requireUserId() {
    String userId = currentUserProvider.getCurrentUserId();
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("Unauthenticated");
    }
    return userId;
  }
}
