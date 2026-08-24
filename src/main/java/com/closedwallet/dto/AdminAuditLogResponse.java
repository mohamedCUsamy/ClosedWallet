package com.closedwallet.dto;

import com.closedwallet.enums.AdminAction;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class AdminAuditLogResponse {
    private Long id;
    private Long adminId;
    private AdminAction action;
    private String targetEntity;
    private LocalDateTime timestamp;
}
