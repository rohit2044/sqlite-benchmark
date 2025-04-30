package org.example.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.dto.RequestType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author r0l099q
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "job_queue", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_consumer_id", columnList = "consumer_id"),
        @Index(name = "idx_request_type", columnList = "request_type")
})
public class Job {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tracking_id")
  private Long trackingId;

  @Column(name = "status")
  private Integer status;

  @Column(name = "created_ts")
  private Timestamp createdTs;

  @Column(name = "modified_at")
  private Timestamp modifiedAt;

  @Column(name = "completion_ts")
  private Timestamp completionTs;

  // Changed to handle JSON data (H2 doesn't support native JSON type, but can store as VARCHAR/TEXT)
  @Column(name = "metadata", columnDefinition = "VARCHAR")
  private String metadata;

  // Changed to UUID type to match schema
  @Column(name = "consumer_id")
  private UUID consumerId;

  @Enumerated(EnumType.STRING)
  @Column(name = "request_type")
  private RequestType requestType;

  @Column(name = "created_by", columnDefinition = "VARCHAR")
  private String createdBy;

  @Column(name = "last_modified_user", columnDefinition = "VARCHAR")
  private String lastModifiedUser;

  @Column(name = "last_modified_process", columnDefinition = "VARCHAR")
  private String lastModifiedProcess;

  @Column(name = "est_completion_ts")
  private Timestamp estCompletionTs;

  // Changed to handle JSON data (H2 doesn't have native JSONB, using VARCHAR/CLOB)
  @Column(name = "input", columnDefinition = "CLOB")
  private String input;

  @Column(name = "output", columnDefinition = "CLOB")
  private String output;

}
