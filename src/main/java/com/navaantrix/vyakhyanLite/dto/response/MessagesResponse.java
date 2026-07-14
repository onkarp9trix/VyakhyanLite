package com.navaantrix.vyakhyanLite.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class MessagesResponse {

    private Long messagesId;

    private Long conversationId;

    private String role;

    private String contentText;

    private List<String> contentMedia;

    private Long parentId;

    private Instant createdAt;

    private List<String> fileName;

    private String rating;

    private String comment;

    private Long statusId;

    private Map<String,Object> analyzeResponse;

    private String fileUsed;

    private String userId;
}
