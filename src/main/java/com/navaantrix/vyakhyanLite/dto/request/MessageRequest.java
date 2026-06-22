package com.navaantrix.vyakhyanLite.dto.request;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class MessageRequest {

    private Long conversationId;

    private MultipartFile file;

}
