package com.navaantrix.vyakhyanLite.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
public class ConversationResponse {


    private Long conversationId;

    private String title;

    private Instant createdAt;

    private boolean isArchived;

    private String userId;

    private List<String> fileName;

    private Long statusId;
}
