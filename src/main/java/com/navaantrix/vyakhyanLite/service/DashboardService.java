package com.navaantrix.vyakhyanLite.service;

import com.navaantrix.vyakhyanLite.dto.request.DashboardRequest;
import com.navaantrix.vyakhyanLite.dto.response.DashboardResponse;

import java.util.List;

public interface DashboardService {

    DashboardResponse saveDashBoard(DashboardRequest request);

    List<DashboardResponse> getDashboardByConversationIdAndStatusActive(Long conversationId);

    DashboardResponse deleteDashBoard(Long dashBoardId);

    DashboardResponse updateDashBoardName(Long dashBoardId, DashboardRequest request);



}
