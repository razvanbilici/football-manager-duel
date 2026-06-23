package com.football.dto;
import lombok.Data;
@Data public class NotificationResponse {
    public Long id;
    public String type;
    public String message;
    public Long referenceId;
    public boolean read;
    public String createdAt;
    public ProposalResponse proposal;
}
