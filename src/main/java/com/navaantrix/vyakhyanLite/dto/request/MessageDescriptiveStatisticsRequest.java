package com.navaantrix.vyakhyanLite.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class MessageDescriptiveStatisticsRequest {

    private String file_name;

    private Long conversationId;
}
