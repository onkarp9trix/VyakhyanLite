package com.navaantrix.vyakhyanLite.dto.request;

import com.navaantrix.vyakhyanLite.entity.Conversation;
import com.navaantrix.vyakhyanLite.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class DashboardRequest {

    private String dashboardName;

    private String dashboardFileName;

    private Long conversationId;

}
