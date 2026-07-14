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

@Entity
@Data
@Table(name = "messages")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messages_id")
    private Long messagesId;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;


    @Column(name = "role")
    private String role;

    @Column(name = "content_text",columnDefinition = "TEXT")
    private String contentText;

    @Column(name = "content_media")
    private List<String> contentMedia;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "rating")
    private String rating;

    @Column(name = "comment",columnDefinition = "TEXT")
    private String comment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analyze_response")
    private Map<String,Object> analyzeResponse;

    @Column(name = "file_used+")
    private String fileUsed;

    @Column(name = "user_id")
    private String userId;



}
