package com.pdfdesk.service.sharing.service;

import com.pdfdesk.service.audit.service.AuditLogService;
import com.pdfdesk.service.document.model.Document;
import com.pdfdesk.service.document.service.DocumentService;
import com.pdfdesk.service.permission.model.DocumentRole;
import com.pdfdesk.service.permission.service.PermissionService;
import com.pdfdesk.service.sharing.model.DocumentShareLink;
import com.pdfdesk.service.sharing.repository.DocumentShareLinkRepository;
import com.pdfdesk.service.users.model.User;
import com.pdfdesk.service.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SharingService {
  private final DocumentService documentService;
  private final PermissionService permissionService;
  private final UserRepository userRepository;
  private final DocumentShareLinkRepository linkRepository;
  private final AuditLogService auditLogService;
  private final KafkaTemplate<String, String> kafkaTemplate;

  public boolean shareWithUser(String actorUserId, String documentId, String email, DocumentRole role) {
    Document document = documentService.getById(documentId);
    permissionService.requireAtLeastEditor(actorUserId, document);

    User targetUser = userRepository.findByEmail(email.trim().toLowerCase());
    if (targetUser == null) {
      return false;
    }

    permissionService.upsertUserGrant(actorUserId, documentId, targetUser.getId(), role);
    auditLogService.record(documentId, actorUserId, "SHARE_WITH_USER", Map.of(
        "targetUserId", targetUser.getId(),
        "role", role.name()
    ));
    kafkaTemplate.send("document.notifications", "shared:" + documentId + ":" + targetUser.getId());
    return true;
  }

  public String createLinkShare(String actorUserId, String documentId, DocumentRole role, String expiresAt) {
    Document document = documentService.getById(documentId);
    permissionService.requireAtLeastEditor(actorUserId, document);

    String rawToken = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    DocumentShareLink link = new DocumentShareLink();
    link.setDocumentId(documentId);
    link.setTokenHash(sha256(rawToken));
    link.setRole(role);
    link.setExpiresAt(expiresAt);
    link.setCreatedAt(Instant.now().toString());
    link.setCreatedBy(actorUserId);
    linkRepository.save(link);

    auditLogService.record(documentId, actorUserId, "CREATE_LINK_SHARE", Map.of(
        "role", role.name(),
        "expiresAt", expiresAt
    ));
    return rawToken;
  }

  private String sha256(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 unavailable", e);
    }
  }
}
