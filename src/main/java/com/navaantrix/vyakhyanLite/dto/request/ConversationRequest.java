package com.navaantrix.vyakhyanLite.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ConversationRequest {


    private String title;

    private boolean isArchived;

    private String userId;

}
