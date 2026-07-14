package com.navaantrix.vyakhyanLite.dto.request;

import lombok.Data;

@Data
public class MessageFeedBackRequest {

    private Long messagesId;

    private String rating;

    private String Comment;
}
