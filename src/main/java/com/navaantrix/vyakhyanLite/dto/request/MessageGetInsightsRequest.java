package com.navaantrix.vyakhyanLite.dto.request;


import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class MessageGetInsightsRequest {

    private Long conversationId;

    private Long messagesId;

    private Map<String,Object> inside;

}
