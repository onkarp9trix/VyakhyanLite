package com.navaantrix.vyakhyanLite.service;

import com.navaantrix.vyakhyanLite.dto.request.*;
import com.navaantrix.vyakhyanLite.dto.response.MessagesResponse;


import java.util.List;


public interface MessagesService {

    MessagesResponse saveFile(MessageRequest request, String userId);

    MessagesResponse descriptiveStatistics(MessageDescriptiveStatisticsRequest request, String userId);

    MessagesResponse runQuery(MessageRunQueryRequest request, String userId);

    List<MessagesResponse> getMessageByConversationId(Long conversationId);

    MessagesResponse viewData(MessageViewDataRequest request, String userId);

    MessagesResponse fetchSchema(MessageFetchSchemaRequest request, String userId);

    MessagesResponse generateDashboard(generateDashboardRequest request, String userId);

    MessagesResponse getInsights(MessageGetInsightsRequest request, String userId);

}
