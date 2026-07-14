package com.navaantrix.vyakhyanLite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Entity
@Table(name = "conversation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long conversationId;

    @Column(name = "title")
    private String title;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "is_activation")
    private boolean isArchived;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "file_names")
    private List<String> fileName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "file_name_and_schema")
    private Map<String ,Object> fileNameAndSchema;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;



}
