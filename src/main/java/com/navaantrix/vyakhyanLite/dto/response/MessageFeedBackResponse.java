package com.navaantrix.vyakhyanLite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MessageFeedBackResponse {
    private Long messagesId;

    private String rating;

    private String Comment;
}
