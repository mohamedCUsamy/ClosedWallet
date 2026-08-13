package com.closedwallet.Entity;

import java.time.LocalDateTime;

import com.closedwallet.enums.AdminAction;

import jakarta.persistence.*;
@Entity
public class AdminAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long adminId;
    @Enumerated(EnumType.STRING)
    private AdminAction action;
    private String targetEntity;
    private LocalDateTime timestamp;
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
    public AdminAuditLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public AdminAction getAction() {
        return action;
    }

    public void setAction(AdminAction action) {
        this.action = action;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

