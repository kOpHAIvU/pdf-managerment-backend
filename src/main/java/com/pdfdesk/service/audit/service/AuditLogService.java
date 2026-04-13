package com.pdfdesk.service.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdfdesk.service.audit.model.AuditLogEvent;
import com.pdfdesk.service.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {
  private final AuditLogRepository auditLogRepository;
  private final ObjectMapper objectMapper;

  public void record(String documentId, String actorUserId, String action, Map<String, Object> metadata) {
    AuditLogEvent event = new AuditLogEvent();
    event.setDocumentId(documentId);
    event.setEventId("EVT#" + UUID.randomUUID());
    event.setActorUserId(actorUserId);
    event.setAction(action);
    event.setMetadata(toJson(metadata));
    event.setCreatedAt(Instant.now().toString());
    auditLogRepository.save(event);
  }

  private String toJson(Map<String, Object> metadata) {
    try {
      return objectMapper.writeValueAsString(metadata);
    } catch (JsonProcessingException ignored) {
      return "{}";
    }
  }
}
