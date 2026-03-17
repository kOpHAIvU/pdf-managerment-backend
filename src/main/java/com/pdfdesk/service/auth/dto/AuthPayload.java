package com.pdfdesk.service.auth.dto;

import com.pdfdesk.service.users.model.User;

public record AuthPayload(String token, User user) {

}