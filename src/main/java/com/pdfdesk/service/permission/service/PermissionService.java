package com.pdfdesk.service.permission.service;

import com.pdfdesk.service.common.ForbiddenException;
import com.pdfdesk.service.document.model.Document;
import com.pdfdesk.service.permission.model.DocumentPermissionGrant;
import com.pdfdesk.service.permission.model.DocumentRole;
import com.pdfdesk.service.permission.repository.DocumentPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PermissionService {
  private final DocumentPermissionRepository permissionRepository;
  private final Map<String, CachedRole> roleCache = new ConcurrentHashMap<>();

  public DocumentRole resolveRole(String userId, Document document) {
    if (document.getOwnerUserId().equals(userId)) {
      return DocumentRole.OWNER;
    }
    String cacheKey = document.getId() + ":" + userId;
    CachedRole cached = roleCache.get(cacheKey);
    if (cached != null && cached.expiresAtEpochMs > System.currentTimeMillis()) {
      return cached.role;
    }

    List<DocumentPermissionGrant> grants = permissionRepository.findByDocumentId(document.getId());
    DocumentRole resolvedRole = grants.stream()
        .filter(grant -> ("USER#" + userId).equals(grant.getPrincipal()))
        .map(DocumentPermissionGrant::getRole)
        .max(Comparator.comparingInt(DocumentRole::ordinal))
        .orElse(null);

    roleCache.put(cacheKey, new CachedRole(resolvedRole, System.currentTimeMillis() + 60_000));
    return resolvedRole;
  }

  public void requireAtLeastEditor(String userId, Document document) {
    DocumentRole role = resolveRole(userId, document);
    if (role == null || role.ordinal() < DocumentRole.EDITOR.ordinal()) {
      throw new ForbiddenException("Missing EDITOR permission");
    }
  }

  public void requireCanRead(String userId, Document document) {
    if (resolveRole(userId, document) == null) {
      throw new ForbiddenException("Missing READ permission");
    }
  }

  public void upsertUserGrant(String actorUserId, String documentId, String targetUserId, DocumentRole role) {
    DocumentPermissionGrant grant = new DocumentPermissionGrant();
    grant.setDocumentId(documentId);
    grant.setPrincipal("USER#" + targetUserId);
    grant.setRole(role);
    grant.setGrantedBy(actorUserId);
    grant.setGrantedAt(Instant.now().toString());
    permissionRepository.upsert(grant);
    roleCache.remove(documentId + ":" + targetUserId);
  }

  private record CachedRole(DocumentRole role, long expiresAtEpochMs) {
  }
}
