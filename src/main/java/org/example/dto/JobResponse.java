package org.example.dto;

import lombok.Data;

@Data
public class JobResponse {
    private String status;
    private String message;
    private Long trackingId;
}
