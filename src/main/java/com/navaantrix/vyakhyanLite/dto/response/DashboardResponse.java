package com.navaantrix.vyakhyanLite.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class DashboardResponse {

    private Long dashboardId;

    private String dashboardName;

    private Instant saveAt;

    private String dashboardFileName;

    private Long conversationId;

    private Long statusId;

}
