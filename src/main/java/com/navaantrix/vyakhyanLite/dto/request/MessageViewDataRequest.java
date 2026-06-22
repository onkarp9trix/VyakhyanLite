package com.navaantrix.vyakhyanLite.dto.request;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class MessageViewDataRequest {

    private Long conversationId;
    private String file_name;
    private Long page;
    private Long size;
}
